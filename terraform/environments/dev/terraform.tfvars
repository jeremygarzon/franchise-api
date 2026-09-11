# Values for the dev environment. Override any of these as needed.
project           = "franchise-api"
region            = "us-east-1"
availability_zones = ["us-east-1a", "us-east-1b"]

app_port  = 8080
image_tag = "latest"

# "demo" loads the sample data (data.sql) so the API has data right after deployment.
# Switch to "prod" for a real production deployment that should not seed demo data.
spring_profile = "demo"

db_name           = "franchise"
db_username       = "franchise_admin"
db_instance_class = "db.t3.micro" # free-tier eligible (750 h/month, first year)

# Keep it small for a cost-conscious dev/demo: a single task, scaling up to 2 under load.
# Auto scaling is still exercised (min != max) but the idle footprint stays minimal.
desired_count = 1
min_capacity  = 1
max_capacity  = 2
