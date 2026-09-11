output "state_bucket_name" {
  description = "S3 bucket name to reference in each environment's backend block."
  value       = aws_s3_bucket.state.id
}
