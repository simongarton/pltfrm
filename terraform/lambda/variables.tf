variable "lambda_runtime" {
  default = "java11"
}

variable "weather_lambda_filename" {
  default = "../weather/target/weather.zip"
}

variable "openweathermap-url" {}

variable "openweathermap-api-key" {}