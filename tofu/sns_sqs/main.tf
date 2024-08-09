resource "aws_sns_topic" "this" {

  name = var.topic_name

  policy = <<POLICY
{
    "Version":"2012-10-17",
    "Statement":[{
        "Effect": "Allow",
        "Principal": { "Service": "s3.amazonaws.com" },
        "Action": "SNS:Publish",
        "Resource": "arn:aws:sns:*:*:conversion-topic"
    }]
}
POLICY

  tags = {
    Name    = var.topic_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_sqs_queue_policy" "this" {

  queue_url = aws_sqs_queue.this.url

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Id": "sqspolicy",
  "Statement": [
    {
      "Sid": "First",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "sqs:SendMessage",
      "Resource": "${aws_sqs_queue.this.arn}",
      "Condition": {
        "ArnEquals": {
          "aws:SourceArn": "${aws_sns_topic.this.arn}"
        }
      }
    }
  ]
}
POLICY
}

resource "aws_sqs_queue" "this" {

  name                       = var.queue_name
  visibility_timeout_seconds = 120

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.this-dlq.arn
    maxReceiveCount     = 4
  })

  tags = {
    Name    = var.queue_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_sns_topic_subscription" "this" {
  topic_arn = aws_sns_topic.this.arn
  endpoint  = aws_sqs_queue.this.arn

  protocol = "sqs"
}

resource "aws_ssm_parameter" "this" {
  name  = var.topic_name_for_ssm
  type  = "String"
  value = aws_sns_topic.this.arn

  tags = {
    Name    = var.topic_name_for_ssm
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

resource "aws_sqs_queue" "this-dlq" {

  name                       = "${var.queue_name}-dlq"
  delay_seconds              = 0
  max_message_size           = 262144
  message_retention_seconds  = 86400
  receive_wait_time_seconds  = 0
  visibility_timeout_seconds = 300

  tags = {
    Name    = "${var.queue_name}-dlq"
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}