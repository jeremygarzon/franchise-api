output "alb_dns_name" {
  description = "Public DNS name of the load balancer (the API's public URL)."
  value       = aws_lb.this.dns_name
}

output "cluster_name" {
  description = "ECS cluster name (used for force-new-deployment commands)."
  value       = aws_ecs_cluster.this.name
}

output "service_name" {
  description = "ECS service name (used for force-new-deployment commands)."
  value       = aws_ecs_service.this.name
}
