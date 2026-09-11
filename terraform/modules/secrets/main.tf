locals {
  name = "${var.project}-${var.environment}"
  tags = {
    Project     = var.project
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

# Generate a strong random password so no credential is ever written by hand or
# committed to source control. Characters are restricted to values that are safe
# inside an R2DBC/JDBC URL.
resource "random_password" "db" {
  length           = 24
  special          = true
  override_special = "!#$%&*()-_=+[]{}"
}

# The secret container in AWS Secrets Manager.
resource "aws_secretsmanager_secret" "db" {
  name        = "${local.name}-db-credentials"
  description = "Database credentials for ${local.name}, consumed by ECS at runtime."
  tags        = local.tags
}

# The actual secret value: a JSON document the container reads key by key.
resource "aws_secretsmanager_secret_version" "db" {
  secret_id = aws_secretsmanager_secret.db.id
  secret_string = jsonencode({
    username = var.db_username
    password = random_password.db.result
    dbname   = var.db_name
  })
}

# IAM policy document that grants read access to THIS secret only (least privilege).
# The ECS task execution role attaches this so it can inject the secret at start-up.
data "aws_iam_policy_document" "read_secret" {
  statement {
    sid       = "ReadDbSecret"
    effect    = "Allow"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [aws_secretsmanager_secret.db.arn]
  }
}

resource "aws_iam_policy" "read_secret" {
  name        = "${local.name}-read-db-secret"
  description = "Allows reading only the ${local.name} database secret."
  policy      = data.aws_iam_policy_document.read_secret.json
  tags        = local.tags
}
