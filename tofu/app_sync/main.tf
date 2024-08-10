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

resource "aws_appsync_datasource" "weather_datasource" {

  api_id           = aws_appsync_graphql_api.this.id
  name             = "weather_datasource"
  service_role_arn = aws_iam_role.appsync_dynamodb_role.arn

  type = "AMAZON_DYNAMODB"
  dynamodb_config {
    table_name = var.weather_table_name
    region     = var.region
  }
}

resource "aws_appsync_datasource" "hourly_weather_datasource" {

  api_id           = aws_appsync_graphql_api.this.id
  name             = "hourly_weather_datasource"
  service_role_arn = aws_iam_role.appsync_dynamodb_role.arn

  type = "AMAZON_DYNAMODB"
  dynamodb_config {
    table_name = var.hourly_weather_table_name
    region     = var.region
  }
}

resource "aws_appsync_resolver" "get_weather" {

  api_id = aws_appsync_graphql_api.this.id
  type   = "Query"
  field  = "getWeather"

  data_source = aws_appsync_datasource.weather_datasource.name

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

resource "aws_appsync_resolver" "get_hourly_weather" {

  api_id = aws_appsync_graphql_api.this.id
  type   = "Query"
  field  = "getHourlyWeather"

  data_source = aws_appsync_datasource.hourly_weather_datasource.name

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