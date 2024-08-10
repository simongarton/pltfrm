module "lambda" {

  source = "./lambda"

  openweathermap-api-key = "SECRET"
  openweathermap-url     = "https://api.openweathermap.org/data/3.0/onecall"

  application_key_arn  = module.kms.application_key_arn
  timestream_table_arn = module.timestream.day_table.arn
  weather_queue_arn    = module.sns_sqs.queue.arn
  dynamodb_table_arns  = module.dynamodb.dynamodb_table_arns
  event_bus_arn        = module.eventbridge.event_bus_arn
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
  event_bus_name             = "pltfrm-weather-event-bus"
  event_sqs_queue_arn        = module.event_sqs.queue.arn
  event_sqs_queue_url        = module.event_sqs.queue.url
  weather_rule_description   = "Weather Events"
  weather_rule_name          = "weather_event_rule"
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

module "event_sqs" {

  source     = "./sqs"
  queue_name = "pltfrm-weather-event-queue"
}

module "sns_sqs" {

  source             = "./sns_sqs"
  queue_name         = "pltfrm-weather-file-queue"
  topic_name         = "pltfrm-weather-file-topic"
  topic_name_for_ssm = "/pltfrm/weather-topic-arn"
}

module "timestream" {
  source = "./timestream"

  database_name  = "pltfrm-weather"
  day_table_name = "pltfrm-weather-day-table"

  database_name_for_ssm = "/pltfrm/weather-timestream-database-name"
}

module "dynamodb" {

  source = "./dynamodb"

}