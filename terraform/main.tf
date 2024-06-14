module "lambda" {

  source = "./lambda"

  openweathermap-api-key = "SECRET"
  openweathermap-url     = "https://api.openweathermap.org/data/3.0/onecall"

  application_key_arn     = module.kms.application_key_arn
  timestream_database_arn = module.timestream.database.arn
  weather_queue_arn       = module.sns_sqs.queue.arn

  dynamodb_table_arns = module.dynamodb.dynamodb_table_arns

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


  bucket_name_for_ssm = "/pltfrm/weather-bucket-name"
}

module "sns_sqs" {

  source             = "./sns_sqs"
  queue_name         = "pltfrm-weather-file-queue"
  topic_name         = "pltfrm-weather-file-topic"
  topic_name_for_ssm = "/pltfrm/weather-topic-arn"
}

module "timestream" {
  source = "./timestream"

  database_name = "pltfrm-weather"
  table_names   = [
    "pltfrm-weather-day-table"
  ]

  database_name_for_ssm = "/pltfrm/weather-timestream-database-name"
}

module "dynamodb" {

  source = "./dynamodb"

}