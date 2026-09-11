terraform {
  required_version = ">= 1.11.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }

  # Remote state stored in the S3 bucket created by terraform/bootstrap.
  # State locking is handled natively by S3 (use_lockfile), so no DynamoDB table is needed.
  # Each environment uses a distinct `key`, so dev/staging/prod never share state.
  backend "s3" {
    bucket       = "franchise-api-tfstate-prueba123321"
    key          = "dev/terraform.tfstate"
    region       = "us-east-1"
    use_lockfile = true
    encrypt      = true
  }
}

provider "aws" {
  region = var.region
}
