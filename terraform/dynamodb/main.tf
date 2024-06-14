resource "aws_dynamodb_table" "pltfrm_weather_weather_table" {
  name         = "pltfrm-weather-weather-table"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "Timestamp"

  attribute {
    name = "Timestamp"
    type = "S"
  }

  tags = {
    Name    = "pltfrm-weather-weather-table"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}

resource "aws_dynamodb_table" "pltfrm_weather_forecast_hour_table" {
  name         = "pltfrm-weather-forecast-hour-table"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "Timestamp"

  attribute {
    name = "Timestamp"
    type = "S"
  }

  tags = {
    Name    = "pltfrm-weather-forecast-hour-table"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}

resource "aws_dynamodb_table" "pltfrm_weather_forecast_day_table" {
  name         = "pltfrm-weather-forecast-day-table"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "Timestamp"

  attribute {
    name = "Timestamp"
    type = "S"
  }

  tags = {
    Name    = "pltfrm-weather-forecast-day-table"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}

resource "aws_dynamodb_table" "pltfrm_weather_log_table" {
  name         = "pltfrm-weather-log-table"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "WeatherTable"

  attribute {
    name = "WeatherTable"
    type = "S"
  }

  tags = {
    Name    = "pltfrm-weather-log-table"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}