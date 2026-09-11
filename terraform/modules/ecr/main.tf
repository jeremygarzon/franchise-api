locals {
  name = "${var.project}-${var.environment}"
  tags = {
    Project     = var.project
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

# Private repository that holds the application's Docker images.
resource "aws_ecr_repository" "this" {
  name                 = local.name
  image_tag_mutability = "MUTABLE"

  # Allow `terraform destroy` to remove the repository even if it still holds images.
  # Convenient for a dev/demo environment that is torn down repeatedly.
  force_delete = true

  # Scan images for known vulnerabilities on push.
  image_scanning_configuration {
    scan_on_push = true
  }

  tags = local.tags
}

# Expire old images so the repository does not grow unbounded; keep the N newest.
resource "aws_ecr_lifecycle_policy" "this" {
  repository = aws_ecr_repository.this.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Keep only the ${var.image_retention_count} most recent images"
        selection = {
          tagStatus   = "any"
          countType   = "imageCountMoreThan"
          countNumber = var.image_retention_count
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}
