locals {
  name = "${var.project}-${var.environment}"
  tags = {
    Project     = var.project
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

# Subnet group for the database. The instance sets publicly_accessible = false and its
# security group only allows the ECS tasks in, so it is not reachable from the internet
# even though these subnets are public (a cost trade-off to avoid a NAT gateway).
resource "aws_db_subnet_group" "this" {
  name       = "${local.name}-db-subnet-group"
  subnet_ids = var.subnet_ids
  tags       = merge(local.tags, { Name = "${local.name}-db-subnet-group" })
}

# PostgreSQL instance. Not publicly accessible; reachable only from the ECS tasks
# via the security group. Credentials come from the secrets module (never hard-coded).
resource "aws_db_instance" "this" {
  identifier     = "${local.name}-db"
  engine         = "postgres"
  engine_version = var.engine_version
  instance_class = var.instance_class

  allocated_storage = var.allocated_storage
  storage_type      = "gp3"
  storage_encrypted = true

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password
  port     = 5432

  db_subnet_group_name   = aws_db_subnet_group.this.name
  vpc_security_group_ids = [var.security_group_id]
  publicly_accessible    = false
  multi_az               = false # single-AZ for a dev environment; enable in prod

  backup_retention_period = var.backup_retention_period # 0 for Free Tier; raise in production
  skip_final_snapshot     = true                        # convenient for dev; set false + a snapshot id in prod
  deletion_protection     = false

  tags = merge(local.tags, { Name = "${local.name}-db" })
}
