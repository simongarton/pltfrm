output "application_key_id" {
  value = aws_kms_key.application_key.id
}

output "application_key_arn" {
  value = aws_kms_key.application_key.arn
}