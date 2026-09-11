variable "project" {
  type        = string
  description = "Project name used for naming and tagging."
}

variable "environment" {
  type        = string
  description = "Environment name (e.g. dev, staging, prod)."
}

variable "db_username" {
  type        = string
  description = "Master username for the database."
  default     = "franchise_admin"
}

variable "db_name" {
  type        = string
  description = "Name of the application database."
  default     = "franchise"
}
