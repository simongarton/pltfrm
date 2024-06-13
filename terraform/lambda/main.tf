resource "aws_lambda_function" "weather_api" {
  function_name                  = "pltfrm-weather-api"
  filename                       = var.weather_lambda_filename
  role                           = aws_iam_role.pltfrm_lambda_iam.arn
  handler                        = "com.simongarton.pltfrm.weather.lambda.WeatherLambdaAPIGatewayRequestHandler"
  source_code_hash               = filebase64sha256(var.weather_lambda_filename)
  runtime                        = var.lambda_runtime
  memory_size                    = 512
  timeout                        = 90
  reserved_concurrent_executions = 1
  tags                           = {
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
  source_code_hash               = filebase64sha256(var.weather_lambda_filename)
  runtime                        = var.lambda_runtime
  memory_size                    = 512
  timeout                        = 90
  reserved_concurrent_executions = 1
  tags                           = {
    Name    = "Weather EventBridge Lambda"
    Project = "pltfrm"
    Owner   = "simon.garton@gmail.com"
  }
}