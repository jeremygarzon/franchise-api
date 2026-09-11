variable "project" {
  type        = string
  description = "Project name used for naming and tagging."
}

variable "environment" {
  type        = string
  description = "Environment name (e.g. dev, staging, prod)."
}

variable "subnet_ids" {
  type        = list(string)
  description = "Subnet IDs the DB subnet group spans. The instance is not publicly accessible regardless; its security group is what restricts access."
}

variable "security_group_id" {
  type        = string
  description = "Security group that allows access from the ECS tasks."
}

variable "db_name" {
  type        = string
  description = "Name of the application database to create."
}

variable "db_username" {
  type        = string
  description = "Master username."
}

variable "db_password" {
  type        = string
  description = "Master password (sourced from the secrets module)."
  sensitive   = true
}

variable "instance_class" {
  type        = string
  description = "RDS instance size."
  default     = "db.t3.micro"
}

variable "allocated_storage" {
  type        = number
  description = "Allocated storage in GB."
  default     = 20
}

variable "engine_version" {
  type        = string
  description = "PostgreSQL engine version."
  default     = "16.4"
}

variable "backup_retention_period" {
  type        = number
  description = "Days to retain automated backups. AWS Free Tier requires 0 (backups disabled). Set >0 in production."
  default     = 0
}
