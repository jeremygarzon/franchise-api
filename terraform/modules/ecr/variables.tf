variable "project" {
  type        = string
  description = "Project name used for the repository name and tagging."
}

variable "environment" {
  type        = string
  description = "Environment name (e.g. dev, staging, prod)."
}

variable "image_retention_count" {
  type        = number
  description = "How many most-recent images to keep before expiring older ones."
  default     = 10
}
