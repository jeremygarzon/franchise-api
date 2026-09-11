output "secret_arn" {
  description = "ARN of the Secrets Manager secret holding the DB credentials."
  value       = aws_secretsmanager_secret.db.arn
}

output "read_secret_policy_arn" {
  description = "ARN of the IAM policy that grants read access to the DB secret."
  value       = aws_iam_policy.read_secret.arn
}

output "db_username" {
  description = "Database master username (non-sensitive)."
  value       = var.db_username
}

output "db_name" {
  description = "Application database name."
  value       = var.db_name
}

output "db_password" {
  description = "Generated database password (sensitive; consumed by the RDS module)."
  value       = random_password.db.result
  sensitive   = true
}
