output "address" {
  description = "DNS address (host) of the RDS instance."
  value       = aws_db_instance.this.address
}

output "port" {
  description = "Port the database listens on."
  value       = aws_db_instance.this.port
}

output "db_name" {
  description = "Name of the application database."
  value       = aws_db_instance.this.db_name
}

output "r2dbc_url" {
  description = "R2DBC connection URL the application uses to reach the database. RDS requires TLS, so SSL is enabled (sslMode=require encrypts without verifying the CA, which is enough here)."
  value       = "r2dbc:postgresql://${aws_db_instance.this.address}:${aws_db_instance.this.port}/${aws_db_instance.this.db_name}?ssl=true&sslMode=require"
}
