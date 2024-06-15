resource "aws_dynamodb_table" "pltfrm_weather_weather_table" {
  name         = "PltfrmWeatherWeather"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "timestamp"

  attribute {
    name = "timestamp"
    type = "S"
  }

  tags = {
    Name    = "PltfrmWeatherWeather"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}

resource "aws_dynamodb_table" "pltfrm_weather_forecast_hour_table" {
  name         = "PltfrmWeatherForecastHour"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "timestamp"

  attribute {
    name = "timestamp"
    type = "S"
  }

  tags = {
    Name    = "PltfrmWeatherForecastHour"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}

resource "aws_dynamodb_table" "pltfrm_weather_forecast_day_table" {
  name         = "PltfrmWeatherForecastDay"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "timestamp"

  attribute {
    name = "timestamp"
    type = "S"
  }

  tags = {
    Name    = "PltfrmWeatherForecastDay"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}

resource "aws_dynamodb_table" "pltfrm_weather_log_table" {
  name         = "PltfrmWeatherLog"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }

  tags = {
    Name    = "PltfrmWeatherLog"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}