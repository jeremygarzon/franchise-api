output "repository_url" {
  description = "URL used to tag and push images (e.g. <acct>.dkr.ecr.<region>.amazonaws.com/<name>)."
  value       = aws_ecr_repository.this.repository_url
}

output "repository_arn" {
  description = "ARN of the repository (referenced by IAM policies)."
  value       = aws_ecr_repository.this.arn
}
