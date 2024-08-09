resource "aws_api_gateway_resource" "weather_resource" {
  path_part   = "weather"
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  rest_api_id = aws_api_gateway_rest_api.api.id
}

resource "aws_api_gateway_method" "weather_get_method" {
  rest_api_id      = aws_api_gateway_rest_api.api.id
  resource_id      = aws_api_gateway_resource.weather_resource.id
  http_method      = "GET"
  authorization    = "NONE"
  api_key_required = true
}

resource "aws_api_gateway_integration" "weather_get_integration" {
  rest_api_id             = aws_api_gateway_rest_api.api.id
  resource_id             = aws_api_gateway_resource.weather_resource.id
  http_method             = aws_api_gateway_method.weather_get_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = var.weather_lambda.invoke_arn
}

resource "aws_lambda_permission" "weather_get_lambda" {
  statement_id  = "AllowPostExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = var.weather_lambda.function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "arn:aws:execute-api:${var.region}:${var.account_id}:${aws_api_gateway_rest_api.api.id}/*/${aws_api_gateway_method.weather_get_method.http_method}${aws_api_gateway_resource.weather_resource.path}"
}
