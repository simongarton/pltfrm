resource "aws_cloudwatch_event_bus" "scheduler-custom-event-bus" {
  name = "scheduler-event-bus"
}

resource "aws_sqs_queue" "scheduler-dlq" {
  name = "scheduler-dlq"
}

resource "aws_scheduler_schedule" "eventbridge-schedule" {
  name = "eventbridge-schedule"

  flexible_time_window {
    mode = "OFF"
  }

  schedule_expression          = "cron(0 * * * ? *)"
  schedule_expression_timezone = "Pacific/Auckland"
  description                  = "Trigger processing"

  target {
    arn      = var.weather_eventbridge_lambda.arn
    role_arn = aws_iam_role.scheduler-role.arn

    dead_letter_config {
      arn = aws_sqs_queue.scheduler-dlq.arn
    }
    
    input = jsonencode(
      { "msg" : "Hello from EventBridge Scheduler!" }
    )
  }
}

resource "aws_iam_policy" "scheduler_policy" {
  name = "scheduler_policy"

  policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Sid" : "VisualEditor0",
          "Effect" : "Allow",
          "Action" : [
            "events:putEvents",
            "sqs:SendMessage"
          ],
          "Resource" : "*"
        }
      ]
    }
  )
}

resource "aws_iam_role" "scheduler-role" {
  name                = "scheduler-role"
  managed_policy_arns = [aws_iam_policy.scheduler_policy.arn]

  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Sid       = ""
        Principal = {
          Service = "scheduler.amazonaws.com"
        }
      },
    ]
  })
}
