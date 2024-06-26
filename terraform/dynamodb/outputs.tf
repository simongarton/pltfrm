output "dynamodb_table_arns" {
  value = [
    aws_dynamodb_table.pltfrm_weather_weather_table.arn,
    aws_dynamodb_table.pltfrm_weather_forecast_hour_table.arn,
    aws_dynamodb_table.pltfrm_weather_forecast_day_table.arn,
    aws_dynamodb_table.pltfrm_weather_log_table.arn
  ]
}
