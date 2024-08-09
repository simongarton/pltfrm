resource "aws_kms_key" "application_key" {
  description             = "pltfrm application key"
  deletion_window_in_days = 30
}

resource "aws_kms_alias" "s3_bucket_key_alias" {
  name          = "alias/s3-bucket-key"
  target_key_id = aws_kms_key.application_key.id
}