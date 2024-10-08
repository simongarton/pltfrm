variable "lambda_runtime" {
  default = "java17"
}

variable "weather_lambda_filename" {
  default = "../weather/target/weather.zip"
}

variable "appsync_lambda_filename" {
  default = "../app_sync_insult_resolver/target/app_sync_insult_resolver.zip"
}

variable "openweathermap-url" {}

variable "openweathermap-api-key" {}

variable "application_key_arn" {}

variable "timestream_table_arn" {}

variable "weather_queue_arn" {}

variable "dynamodb_table_arns" {}

variable "event_bus_arn" {}
