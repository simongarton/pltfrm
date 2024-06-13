module "lambda" {

  source = "./lambda"

  openweathermap-api-key = "SECRET"
  openweathermap-url     = "https://api.openweathermap.org/data/3.0/onecall"

  application_key_arn = module.kms.application_key_arn
}

module "api_gateway" {

  source = "./api_gateway"

  account_id = var.account_id
  region     = var.region

  weather_lambda = module.lambda.weather_api_lambda
}

module "eventbridge" {

  source = "./eventbridge"

  weather_eventbridge_lambda = module.lambda.weather_eventbridge_lambda
}

module "kms" {

  source = "./kms"
}


module "s3" {

  source = "./s3"

  bucket_name        = "pltfrm-weather-file"
  application_key_id = module.kms.application_key_id
}