resource "aws_lambda_function" "weather_api" {
  function_name                  = "pltfrm-weather-api"
  filename                       = var.weather_lambda_filename
  role                           = aws_iam_role.pltfrm_lambda_iam.arn
  handler                        = "com.simongarton.pltfrm.weather.lambda.WeatherLambdaAPIGatewayRequestHandler"
  source_code_hash = filebase64sha256(var.weather_lambda_filename)
  runtime                        = var.lambda_runtime
  memory_size                    = 1024
  timeout                        = 30
  reserved_concurrent_executions = 10
  snap_start {
    apply_on = "PublishedVersions"
  }
  publish = true

  tags = {
    Name    = "Weather APIGateway Lambda"
    Project = "pltfrm"
    Owner   = "simon.garton@gmail.com"
  }
}

resource "aws_lambda_function" "weather_eventbridge" {
  function_name                  = "pltfrm-weather-eventbridge"
  filename                       = var.weather_lambda_filename
  role                           = aws_iam_role.pltfrm_lambda_iam.arn
  handler                        = "com.simongarton.pltfrm.weather.lambda.WeatherLambdaEventBridgeRequestHandler"
  source_code_hash = filebase64sha256(var.weather_lambda_filename)
  runtime                        = var.lambda_runtime
  memory_size                    = 512
  timeout                        = 30
  reserved_concurrent_executions = 1

  tags = {
    Name    = "Weather EventBridge Lambda"
    Project = "pltfrm"
    Owner   = "simon.garton@gmail.com"
  }
}

resource "aws_lambda_function" "weather_sqs_event" {
  function_name                  = "pltfrm-weather-sqs-event"
  filename                       = var.weather_lambda_filename
  role                           = aws_iam_role.pltfrm_lambda_iam.arn
  handler                        = "com.simongarton.pltfrm.weather.lambda.WeatherLambdaSQSEventRequestHandler"
  source_code_hash = filebase64sha256(var.weather_lambda_filename)
  runtime                        = var.lambda_runtime
  memory_size                    = 512
  timeout                        = 30
  reserved_concurrent_executions = 1

  tags = {
    Name    = "Weather SQS Event Lambda"
    Project = "pltfrm"
    Owner   = "simon.garton@gmail.com"
  }
}

resource "aws_lambda_function" "pltfrm_insult_generator" {
  function_name                  = "pltfrm-insult-generator"
  filename                       = var.appsync_lambda_filename
  role                           = aws_iam_role.pltfrm_lambda_iam.arn
  handler                        = "com.simongarton.AppSyncInsultResolver"
  source_code_hash = filebase64sha256(var.appsync_lambda_filename)
  runtime                        = var.lambda_runtime
  memory_size                    = 1024
  timeout                        = 30
  reserved_concurrent_executions = 10
  snap_start {
    apply_on = "PublishedVersions"
  }
  publish = true

  tags = {
    Name    = "AppSync Insult Lambda"
    Project = "pltfrm"
    Owner   = "simon.garton@gmail.com"
  }
}
