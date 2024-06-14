resource "aws_dynamodb_table" "this" {
  name         = "pltfrm_weather_rain_table"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "Timestamp"

  attribute {
    name = "Timestamp"
    type = "S"
  }

  tags = {
    Name    = "pltfrm_weather_rain_table"
    Owner   = "simon.garton@gmail.com"
    Project = "lambda-queue"
  }
}