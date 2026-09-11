output "vpc_id" {
  description = "ID of the VPC."
  value       = aws_vpc.this.id
}

output "public_subnet_ids" {
  description = "IDs of the public subnets (ALB, ECS tasks and RDS)."
  value       = aws_subnet.public[*].id
}

output "alb_security_group_id" {
  description = "Security group ID for the load balancer."
  value       = aws_security_group.alb.id
}

output "ecs_security_group_id" {
  description = "Security group ID for the ECS tasks."
  value       = aws_security_group.ecs.id
}

output "rds_security_group_id" {
  description = "Security group ID for the RDS instance."
  value       = aws_security_group.rds.id
}
