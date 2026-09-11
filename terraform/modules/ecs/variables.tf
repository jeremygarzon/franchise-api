variable "project" {
  type        = string
  description = "Project name used for naming and tagging."
}

variable "environment" {
  type        = string
  description = "Environment name (e.g. dev, staging, prod)."
}

variable "region" {
  type        = string
  description = "AWS region (used for the CloudWatch Logs configuration)."
}

# --- Networking wiring ---
variable "vpc_id" {
  type        = string
  description = "VPC the service runs in."
}

variable "public_subnet_ids" {
  type        = list(string)
  description = "Public subnets for the internet-facing ALB and the Fargate tasks."
}

variable "alb_security_group_id" {
  type        = string
  description = "Security group for the ALB."
}

variable "ecs_security_group_id" {
  type        = string
  description = "Security group for the ECS tasks."
}

# --- Image / runtime wiring ---
variable "image_url" {
  type        = string
  description = "Full image reference to run (ECR repository URL + tag)."
}

variable "app_port" {
  type        = number
  description = "Container port the app listens on."
  default     = 8080
}

variable "health_check_path" {
  type        = string
  description = "HTTP path the ALB uses for health checks."
  default     = "/actuator/health"
}

variable "db_host" {
  type        = string
  description = "Database host (RDS endpoint address). Non-sensitive."
}

variable "db_port" {
  type        = number
  description = "Database port."
  default     = 5432
}

variable "db_name" {
  type        = string
  description = "Application database name. Non-sensitive."
}

variable "spring_profile" {
  type        = string
  description = "Spring profile passed to the container. Use 'demo' to load sample data, 'prod' to skip it."
  default     = "demo"
}

variable "db_secret_arn" {
  type        = string
  description = "ARN of the Secrets Manager secret holding DB username/password."
}

variable "read_secret_policy_arn" {
  type        = string
  description = "IAM policy ARN granting read access to the DB secret."
}

# --- Sizing / scaling ---
variable "task_cpu" {
  type        = number
  description = "Fargate task CPU units (256 = 0.25 vCPU)."
  default     = 512
}

variable "task_memory" {
  type        = number
  description = "Fargate task memory in MB."
  default     = 1024
}

variable "desired_count" {
  type        = number
  description = "Initial number of running tasks."
  default     = 2
}

variable "min_capacity" {
  type        = number
  description = "Minimum tasks for auto scaling."
  default     = 2
}

variable "max_capacity" {
  type        = number
  description = "Maximum tasks for auto scaling."
  default     = 6
}

variable "cpu_target_utilization" {
  type        = number
  description = "Target average CPU % that triggers scaling."
  default     = 65
}
