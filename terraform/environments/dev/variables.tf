variable "project" {
  type        = string
  description = "Project name used for naming and tagging."
  default     = "franchise-api"
}

variable "region" {
  type        = string
  description = "AWS region for all resources."
  default     = "us-east-1"
}

variable "availability_zones" {
  type        = list(string)
  description = "Availability zones for the subnets."
  default     = ["us-east-1a", "us-east-1b"]
}

variable "app_port" {
  type        = number
  description = "Container port the application listens on."
  default     = 8080
}

variable "image_tag" {
  type        = string
  description = "Image tag to deploy from the ECR repository."
  default     = "latest"
}

variable "spring_profile" {
  type        = string
  description = "Spring profile for the container. 'demo' loads sample data; 'prod' skips it."
  default     = "demo"
}

variable "db_name" {
  type        = string
  description = "Application database name."
  default     = "franchise"
}

variable "db_username" {
  type        = string
  description = "Database master username."
  default     = "franchise_admin"
}

variable "db_instance_class" {
  type        = string
  description = "RDS instance size."
  default     = "db.t3.micro"
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
