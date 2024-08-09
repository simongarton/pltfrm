resource "aws_sns_topic" "this" {
  name = var.topic_name

  tags = {
    Name    = var.topic_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltform"
  }
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