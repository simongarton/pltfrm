resource "aws_cloudwatch_event_bus" "this" {
  name = var.event_bus_name

  tags = {
    Name    = var.event_bus_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_cloudwatch_event_rule" "all_events" {
  name           = var.weather_rule_name_all_events
  description    = var.weather_rule_description_all_events
  event_bus_name = var.event_bus_name
  event_pattern = jsonencode({
    "source" : ["com.simongarton.pltfrm.weather.lambda.processor"]
  })

  tags = {
    Name    = var.weather_rule_name_all_events
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_cloudwatch_event_target" "all_events" {
  rule           = aws_cloudwatch_event_rule.all_events.name
  event_bus_name = var.event_bus_name
  target_id      = "sqs-target"
  arn            = var.all_events_sqs_queue_arn
}

resource "aws_cloudwatch_event_rule" "rain" {
  name           = var.weather_rule_name_rain
  description    = var.weather_rule_description_rain
  event_bus_name = var.event_bus_name
  event_pattern = jsonencode({
    "detail" : {
      "current" : {
        "rain" : [{ "exists" : true }]
      }
    }
  })

  tags = {
    Name    = var.weather_rule_name_rain
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_cloudwatch_event_target" "rain" {
  rule           = aws_cloudwatch_event_rule.rain.name
  event_bus_name = var.event_bus_name
  target_id      = "sns-target"
  arn            = var.rain_topic_arn
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
        Resource = var.all_events_sqs_queue_arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_cloudwatch_event_rule.all_events.arn
          }
        }
      },
      {
        Effect = "Allow",
        Principal = {
          Service = "events.amazonaws.com"
        },
        Action   = "sns:Publish",
        Resource = var.rain_topic_arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_cloudwatch_event_rule.rain.arn
          }
        }
      }
    ]
  })
}