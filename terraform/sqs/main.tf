resource "aws_sqs_queue" "this" {

  name = var.queue_name

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.this-dlq.arn
    maxReceiveCount     = 4
  })

  tags = {
    Name    = var.queue_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltform"
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
