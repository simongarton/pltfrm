module "lambda" {

  source = "./lambda"

  openweathermap-api-key = "SECRET"
  openweathermap-url     = "http://api.openweathermap.org/data/3.0/onecall"
}

module "api_gateway" {

  source = "./api_gateway"

  account_id = var.account_id
  region     = var.region

  weather_lambda = module.lambda.weather_lambda
}

