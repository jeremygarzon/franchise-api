locals {
  name = "${var.project}-${var.environment}"
  tags = {
    Project     = var.project
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

# ---------------------------------------------------------------------------
# Cluster + logs
# ---------------------------------------------------------------------------
resource "aws_ecs_cluster" "this" {
  name = "${local.name}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = local.tags
}

resource "aws_cloudwatch_log_group" "this" {
  name              = "/ecs/${local.name}"
  retention_in_days = 14
  tags              = local.tags
}

# ---------------------------------------------------------------------------
# Application Load Balancer (internet-facing) -> target group -> HTTP listener
# ---------------------------------------------------------------------------
resource "aws_lb" "this" {
  name               = "${local.name}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.alb_security_group_id]
  subnets            = var.public_subnet_ids
  tags               = local.tags
}

resource "aws_lb_target_group" "this" {
  name        = "${local.name}-tg"
  port        = var.app_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip" # Fargate registers tasks by IP

  health_check {
    path                = var.health_check_path
    healthy_threshold   = 2
    unhealthy_threshold = 3
    interval            = 30
    timeout             = 5
    matcher             = "200"
  }

  tags = local.tags
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.this.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.this.arn
  }
}

# ---------------------------------------------------------------------------
# Task definition
# ---------------------------------------------------------------------------
resource "aws_ecs_task_definition" "this" {
  family                   = local.name
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.task_cpu
  memory                   = var.task_memory
  execution_role_arn       = aws_iam_role.execution.arn
  task_role_arn            = aws_iam_role.task.arn

  container_definitions = jsonencode([
    {
      name      = local.name
      image     = var.image_url
      essential = true

      portMappings = [
        {
          containerPort = var.app_port
          protocol      = "tcp"
        }
      ]

      # Non-sensitive configuration as plain environment variables.
      # The "demo" profile is intentionally NOT "prod", so data.sql (sample seed) is loaded
      # and the API has data to exercise right after deployment. A real production deployment
      # would use the "prod" profile, which skips the seed.
      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = var.spring_profile },
        { name = "R2DBC_HOST", value = var.db_host },
        { name = "R2DBC_PORT", value = tostring(var.db_port) },
        { name = "R2DBC_DATABASE", value = var.db_name },
        # RDS enforces TLS, so the connection pool must use SSL.
        { name = "R2DBC_SSL_MODE", value = "require" }
      ]

      # Sensitive values injected from Secrets Manager at runtime (never in plain text).
      # The DB secret is a JSON document; each key is referenced with `:key::`.
      secrets = [
        { name = "R2DBC_USERNAME", valueFrom = "${var.db_secret_arn}:username::" },
        { name = "R2DBC_PASSWORD", valueFrom = "${var.db_secret_arn}:password::" }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.this.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = "app"
        }
      }
    }
  ])

  tags = local.tags
}

# ---------------------------------------------------------------------------
# Service (Fargate, public subnets with public IP, behind the ALB)
# ---------------------------------------------------------------------------
resource "aws_ecs_service" "this" {
  name            = "${local.name}-service"
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.this.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.public_subnet_ids
    security_groups  = [var.ecs_security_group_id]
    assign_public_ip = true # public subnet + public IP so tasks reach ECR/CloudWatch without a NAT gateway
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.this.arn
    container_name   = local.name
    container_port   = var.app_port
  }

  # Give the app time to boot before health checks count against it.
  health_check_grace_period_seconds = 60

  depends_on = [aws_lb_listener.http]

  tags = local.tags
}
