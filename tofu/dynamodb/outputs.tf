output "dynamodb_table_arns" {
  value = [
    aws_dynamodb_table.pltfrm_weather_weather_table.arn,
    aws_dynamodb_table.pltfrm_weather_forecast_hour_table.arn,
    aws_dynamodb_table.pltfrm_weather_forecast_day_table.arn,
    aws_dynamodb_table.pltfrm_weather_log_table.arn
  ]
}

output "weather_table_name" {
  value = aws_dynamodb_table.pltfrm_weather_weather_table.name
}

output "hourly_weather_table_name" {
  value = aws_dynamodb_table.pltfrm_weather_forecast_hour_table.name
}
