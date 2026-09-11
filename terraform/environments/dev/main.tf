locals {
  environment = "dev"
}

# ---------------------------------------------------------------------------
# Networking: VPC, subnets, security groups
# ---------------------------------------------------------------------------
module "networking" {
  source = "../../modules/networking"

  project            = var.project
  environment        = local.environment
  availability_zones = var.availability_zones
  app_port           = var.app_port
}

# ---------------------------------------------------------------------------
# ECR: private image repository
# ---------------------------------------------------------------------------
module "ecr" {
  source = "../../modules/ecr"

  project     = var.project
  environment = local.environment
}

# ---------------------------------------------------------------------------
# Secrets: generated DB credentials in Secrets Manager + read policy
# ---------------------------------------------------------------------------
module "secrets" {
  source = "../../modules/secrets"

  project     = var.project
  environment = local.environment
  db_name     = var.db_name
  db_username = var.db_username
}

# ---------------------------------------------------------------------------
# RDS: PostgreSQL, credentials from the secrets module. Not publicly accessible;
# protected by its security group (only the ECS tasks can reach it).
# ---------------------------------------------------------------------------
module "rds" {
  source = "../../modules/rds"

  project           = var.project
  environment       = local.environment
  subnet_ids        = module.networking.public_subnet_ids
  security_group_id = module.networking.rds_security_group_id

  db_name     = module.secrets.db_name
  db_username = module.secrets.db_username
  db_password = module.secrets.db_password

  instance_class = var.db_instance_class
}

# ---------------------------------------------------------------------------
# ECS: Fargate service behind an ALB, reading the DB secret at runtime
# ---------------------------------------------------------------------------
module "ecs" {
  source = "../../modules/ecs"

  project     = var.project
  environment = local.environment
  region      = var.region

  vpc_id                = module.networking.vpc_id
  public_subnet_ids     = module.networking.public_subnet_ids
  alb_security_group_id = module.networking.alb_security_group_id
  ecs_security_group_id = module.networking.ecs_security_group_id

  # Which image to run. Defaults to the repo Terraform created, at :latest.
  image_url = "${module.ecr.repository_url}:${var.image_tag}"
  app_port  = var.app_port

  db_host                = module.rds.address
  db_port                = module.rds.port
  db_name                = module.rds.db_name
  db_secret_arn          = module.secrets.secret_arn
  read_secret_policy_arn = module.secrets.read_secret_policy_arn
  spring_profile         = var.spring_profile

  desired_count = var.desired_count
  min_capacity  = var.min_capacity
  max_capacity  = var.max_capacity
}
