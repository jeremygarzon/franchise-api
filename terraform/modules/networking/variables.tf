variable "project" {
  type        = string
  description = "Project name used for tagging and resource names."
}

variable "environment" {
  type        = string
  description = "Environment name (e.g. dev, staging, prod)."
}

variable "vpc_cidr" {
  type        = string
  description = "CIDR block for the VPC."
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  type        = list(string)
  description = "CIDR blocks for the public subnets (ALB, ECS tasks, RDS). One per AZ."
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "availability_zones" {
  type        = list(string)
  description = "Availability zones to spread the subnets across."
  default     = ["us-east-1a", "us-east-1b"]
}

variable "app_port" {
  type        = number
  description = "Port the application container listens on."
  default     = 8080
}

variable "db_port" {
  type        = number
  description = "Port the PostgreSQL database listens on."
  default     = 5432
}
