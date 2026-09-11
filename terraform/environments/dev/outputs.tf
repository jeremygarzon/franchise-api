output "alb_dns_name" {
  description = "Public URL of the API (http://<this>)."
  value       = module.ecs.alb_dns_name
}

output "ecr_repository_url" {
  description = "ECR repository URL to tag and push the image to."
  value       = module.ecr.repository_url
}

output "ecs_cluster_name" {
  description = "ECS cluster name (for force-new-deployment)."
  value       = module.ecs.cluster_name
}

output "ecs_service_name" {
  description = "ECS service name (for force-new-deployment)."
  value       = module.ecs.service_name
}

output "rds_endpoint" {
  description = "RDS host endpoint (reachable only from the ECS tasks via its security group)."
  value       = module.rds.address
}

output "db_secret_arn" {
  description = "ARN of the Secrets Manager secret holding DB credentials."
  value       = module.secrets.secret_arn
}
