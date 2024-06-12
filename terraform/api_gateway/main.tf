resource "aws_api_gateway_rest_api" "api" {
  name = "pltfrm-api"
  tags = {
    Owner       = "simon.garton@gmail.com"
    Project     = "pltfrm"
    Name        = "pltfrm API"
    Description = "Main API for pltfrm project"
  }
}

resource "aws_api_gateway_deployment" "pltfrm_deployment" {
  rest_api_id       = aws_api_gateway_rest_api.api.id
  stage_description = md5(timestamp())

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "pltfrm_stage" {
  deployment_id = aws_api_gateway_deployment.pltfrm_deployment.id
  rest_api_id   = aws_api_gateway_rest_api.api.id
  stage_name    = "pltfrm"
}

resource "aws_api_gateway_method_settings" "api_settings" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  stage_name  = aws_api_gateway_stage.pltfrm_stage.stage_name
  method_path = "*/*"

  settings {
    metrics_enabled = true
    logging_level   = "INFO"
  }
}

resource "aws_api_gateway_usage_plan" "pltfrm_usage_plan" {
  name = "pltfrm-usage_plan"

  api_stages {
    api_id = aws_api_gateway_rest_api.api.id
    stage  = aws_api_gateway_stage.pltfrm_stage.stage_name
  }

  quota_settings {
    limit  = 20000
    offset = 0
    period = "DAY"
  }

  throttle_settings {
    burst_limit = 5
    rate_limit  = 10
  }
}

resource "aws_api_gateway_api_key" "pltfrm_api_key" {
  name = "pltfrm-api-key"
}

resource "aws_api_gateway_usage_plan_key" "main" {
  key_id        = aws_api_gateway_api_key.pltfrm_api_key.id
  key_type      = "API_KEY"
  usage_plan_id = aws_api_gateway_usage_plan.pltfrm_usage_plan.id
}

resource "aws_ssm_parameter" "pltfrm_api" {
  name  = "/pltfrm/pltfrm-api"
  type  = "String"
  value = aws_api_gateway_stage.pltfrm_stage.invoke_url

  tags = {
    Name    = "API URL"
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_ssm_parameter" "pltfrm_api_key" {
  name  = "/pltfrm/pltfrm-api-key"
  type  = "SecureString"
  value = aws_api_gateway_api_key.pltfrm_api_key.value

  tags = {
    Name    = "API Key"
    Owner   = "simon.garton@gmail.com"
    Project = "PicoMeters HeadEnd"
  }
}
