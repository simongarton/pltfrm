output "weather_api_lambda" {
  value = aws_lambda_function.weather_api
}

output "weather_eventbridge_lambda" {
  value = aws_lambda_function.weather_eventbridge
}


output "insult_generator_lambda" {
  value = aws_lambda_function.pltfrm_insult_generator
}