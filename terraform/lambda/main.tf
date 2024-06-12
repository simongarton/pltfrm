resource "aws_lambda_function" "weather" {
  function_name                  = "pltfrm-weather"
  filename                       = var.weather_lambda_filename
  role                           = aws_iam_role.pltfrm_lambda_iam.arn
  handler                        = "com.simongarton.pltfrm.weather.lambda.WeatherLambdaRequestHandler"
  source_code_hash               = filebase64sha256(var.weather_lambda_filename)
  runtime                        = var.lambda_runtime
  memory_size                    = 512
  timeout                        = 90
  reserved_concurrent_executions = 1
  tags                           = {
    Name    = "Weather Lambda"
    Project = "pltfrm"
    Owner   = "simon.garton@gmail.com"
  }
}