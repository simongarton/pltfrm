resource "aws_lambda_event_source_mapping" "mapping" {
  event_source_arn = var.weather_queue_arn
  function_name    = aws_lambda_function.weather_sqs_event.arn
}
