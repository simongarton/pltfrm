output "dynamodb_table_arns" {
  value = [
    aws_dynamodb_table.pltfrm_weather_rain_table.arn
  ]
}
