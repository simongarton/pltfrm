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
}

resource "aws_sqs_queue_policy" "conversion_policy" {
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
  name = var.queue_name
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
