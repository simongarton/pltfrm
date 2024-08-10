resource "aws_appsync_graphql_api" "this" {

  name                = "pltfrm-weather-graphql-api"
  authentication_type = "API_KEY"

  schema = file("./app_sync/schema.graphql")

  log_config {
    field_log_level          = "ALL"
    cloudwatch_logs_role_arn = aws_iam_role.appsync_logs.arn
  }

  xray_enabled = true
}

resource "aws_iam_role" "appsync_logs" {

  name = "AppSyncLogsRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "appsync.amazonaws.com"
        }
      },
    ],
  })

  managed_policy_arns = [
    "arn:aws:iam::aws:policy/service-role/AWSAppSyncPushToCloudWatchLogs",
  ]
}

resource "aws_appsync_datasource" "this" {

  api_id           = aws_appsync_graphql_api.this.id
  name             = "dynamodb_datasource"
  service_role_arn = aws_iam_role.appsync_dynamodb_role.arn

  dynamodb_config {
    table_name = var.table_name
    region     = var.region
  }
  type = "AMAZON_DYNAMODB"
}

resource "aws_appsync_resolver" "this" {

  api_id = aws_appsync_graphql_api.this.id
  type   = "Query"
  field  = "getWeather"

  data_source = aws_appsync_datasource.this.name

  request_template = <<EOF
  {
    "version": "2017-02-28",
    "operation": "GetItem",
    "key": {
      "timestamp": $util.dynamodb.toDynamoDBJson($ctx.args.timestamp)
    }
  }
  EOF

  response_template = <<EOF
  #if($ctx.error)
    $util.error($ctx.error.message, $ctx.error.type)
  #else
    $util.toJson($ctx.result)
  #end
  EOF
}
