# Trust policy: allow ECS tasks to assume these roles.
data "aws_iam_policy_document" "assume_role" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

# ---------------------------------------------------------------------------
# Task EXECUTION role: used by the ECS agent to pull the image and inject secrets.
# ---------------------------------------------------------------------------
resource "aws_iam_role" "execution" {
  name               = "${local.name}-ecs-execution-role"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
  tags               = local.tags
}

# AWS-managed policy that grants exactly what the agent needs: ECR pull + CloudWatch Logs.
resource "aws_iam_role_policy_attachment" "execution_managed" {
  role       = aws_iam_role.execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Least-privilege addition: read ONLY the DB secret (policy provided by the secrets module).
resource "aws_iam_role_policy_attachment" "execution_read_secret" {
  role       = aws_iam_role.execution.name
  policy_arn = var.read_secret_policy_arn
}

# ---------------------------------------------------------------------------
# TASK role: the identity the application itself runs as. It needs no AWS
# permissions for this service, so it is intentionally left without policies.
# ---------------------------------------------------------------------------
resource "aws_iam_role" "task" {
  name               = "${local.name}-ecs-task-role"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
  tags               = local.tags
}
