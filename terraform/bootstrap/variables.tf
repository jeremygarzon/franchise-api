variable "region" {
  type        = string
  description = "AWS region where the remote-state backend is created."
  default     = "us-east-1"
}

variable "project" {
  type        = string
  description = "Project name used for tagging."
  default     = "franchise-api"
}

variable "state_bucket_name" {
  type        = string
  description = "Globally unique name for the S3 bucket that stores Terraform state."
}
