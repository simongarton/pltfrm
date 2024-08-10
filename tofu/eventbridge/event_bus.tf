resource "aws_cloudwatch_event_bus" "this" {
  name = var.event_bus_name

  tags = {
    Name    = var.event_bus_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_cloudwatch_event_rule" "this" {
  name           = var.weather_rule_name
  description    = var.weather_rule_description
  event_bus_name = var.event_bus_name
  event_pattern = jsonencode({
    "detail-type" : ["weather"],
    "source" : ["WeatherLambdaEventBridgeProcessor"]
  })

  tags = {
    Name    = var.weather_rule_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_cloudwatch_event_target" "this" {
  rule           = aws_cloudwatch_event_rule.this.name
  event_bus_name = var.event_bus_name
  target_id      = "sqs-target"
  arn            = var.event_sqs_queue_arn
}

resource "aws_sqs_queue_policy" "example" {

  queue_url = var.event_sqs_queue_url

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "events.amazonaws.com"
        },
        Action   = "sqs:SendMessage",
        Resource = var.event_sqs_queue_arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_cloudwatch_event_rule.this.arn
          }
        }
      }
    ]
  })
}