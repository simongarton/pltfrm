resource "aws_sns_topic_subscription" "this" {
  topic_arn = var.topic_arn
  protocol  = "email"
  endpoint  = var.email_address
}