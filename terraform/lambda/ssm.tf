resource "aws_ssm_parameter" "openweathermap_url_parameter" {
  name  = "/pltfrm/openweathermap-url"
  type  = "String"
  value = var.openweathermap-url

  tags = {
    Name    = "OpenWeatherMapURL"
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_ssm_parameter" "openweathermap_api_key_parameter" {
  name  = "/pltfrm/openweathermap-api-key"
  type  = "SecureString"
  value = var.openweathermap-api-key

  lifecycle {
    ignore_changes = all
  }

  tags = {
    Name    = "OpenWeatherMapAPIKey"
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

