terraform {
  required_version = ">= 1.11.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  # Intentionally uses LOCAL state: this stack creates the very backend that every
  # other stack will use, so it cannot store its state remotely yet.
}

provider "aws" {
  region = var.region
}

# S3 bucket that holds the remote Terraform state for all environments.
resource "aws_s3_bucket" "state" {
  bucket = var.state_bucket_name

  tags = {
    Project   = var.project
    ManagedBy = "terraform"
    Purpose   = "terraform-remote-state"
  }
}

# Keep a history of state files so a bad apply can be recovered.
resource "aws_s3_bucket_versioning" "state" {
  bucket = aws_s3_bucket.state.id
  versioning_configuration {
    status = "Enabled"
  }
}

# Encrypt the state at rest (it can contain sensitive values).
resource "aws_s3_bucket_server_side_encryption_configuration" "state" {
  bucket = aws_s3_bucket.state.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# Block all public access to the state bucket.
resource "aws_s3_bucket_public_access_block" "state" {
  bucket                  = aws_s3_bucket.state.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# State locking is handled natively by S3 (a lock file in the bucket) via
# `use_lockfile = true` in each environment's backend block, so no DynamoDB
# table is needed. Native S3 locking is GA since Terraform 1.11.
