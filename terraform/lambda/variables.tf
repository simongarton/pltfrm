variable "lambda_runtime" {
  default = "java17"
}

variable "weather_lambda_filename" {
  default = "../weather/target/weather.zip"
}

variable "openweathermap-url" {}

variable "openweathermap-api-key" {}

variable "application_key_arn" {}