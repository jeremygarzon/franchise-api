locals {
  name = "${var.project}-${var.environment}"
  tags = {
    Project     = var.project
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

# ---------------------------------------------------------------------------
# VPC
# ---------------------------------------------------------------------------
resource "aws_vpc" "this" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags                 = merge(local.tags, { Name = "${local.name}-vpc" })
}

# ---------------------------------------------------------------------------
# Public subnets (ALB, ECS tasks and RDS) and Internet Gateway
#
# Cost note: tasks run in PUBLIC subnets with a public IP so they can reach
# ECR/CloudWatch directly through the Internet Gateway. This deliberately avoids
# a NAT Gateway (~$32/month, not free tier). RDS stays protected by its security
# group (only the ECS tasks can reach it), not by network isolation. A production
# setup would use private subnets + NAT.
# ---------------------------------------------------------------------------
resource "aws_internet_gateway" "this" {
  vpc_id = aws_vpc.this.id
  tags   = merge(local.tags, { Name = "${local.name}-igw" })
}

resource "aws_subnet" "public" {
  count                   = length(var.public_subnet_cidrs)
  vpc_id                  = aws_vpc.this.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true
  tags                    = merge(local.tags, { Name = "${local.name}-public-${count.index}" })
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.this.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.this.id
  }
  tags = merge(local.tags, { Name = "${local.name}-public-rt" })
}

resource "aws_route_table_association" "public" {
  count          = length(aws_subnet.public)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# ---------------------------------------------------------------------------
# Security groups (least privilege, chained by reference)
# ---------------------------------------------------------------------------

# ALB: public HTTP in, everything out.
resource "aws_security_group" "alb" {
  name        = "${local.name}-alb-sg"
  description = "Allow inbound HTTP to the load balancer"
  vpc_id      = aws_vpc.this.id

  ingress {
    description = "HTTP from anywhere"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.tags, { Name = "${local.name}-alb-sg" })
}

# ECS tasks: only accept traffic from the ALB on the app port.
resource "aws_security_group" "ecs" {
  name        = "${local.name}-ecs-sg"
  description = "Allow inbound from the ALB to the app port"
  vpc_id      = aws_vpc.this.id

  ingress {
    description     = "App port from the ALB only"
    from_port       = var.app_port
    to_port         = var.app_port
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.tags, { Name = "${local.name}-ecs-sg" })
}

# RDS: only accept traffic from the ECS tasks on the DB port. Not internet-facing.
resource "aws_security_group" "rds" {
  name        = "${local.name}-rds-sg"
  description = "Allow inbound from ECS tasks to the database port"
  vpc_id      = aws_vpc.this.id

  ingress {
    description     = "DB port from ECS tasks only"
    from_port       = var.db_port
    to_port         = var.db_port
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.tags, { Name = "${local.name}-rds-sg" })
}
