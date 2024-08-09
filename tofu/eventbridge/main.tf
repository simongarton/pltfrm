resource "aws_sqs_queue" "scheduler-dlq" {
  name = "pltfrm-eventbridge-scheduler-dlq"
}

resource "aws_scheduler_schedule" "eventbridge-schedule" {
  name = "pltfrm-eventbridge-schedule"

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
  name = "pltfrm-scheduler-policy"

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
        },
        {
          "Sid" : "Lambda",
          "Effect" : "Allow",
          "Action" : [
            "lambda:InvokeFunction"
          ],
          "Resource" : var.weather_eventbridge_lambda.arn
        }
      ]
    }
  )
}

resource "aws_iam_role" "scheduler-role" {
  name                = "pltfrm-scheduler-role"
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
