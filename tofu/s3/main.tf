resource "aws_s3_bucket" "this" {

  bucket = var.bucket_name

  tags = {
    Name    = var.bucket_name
    Project = "pltfrm"
    Owner   = "simon.garton@gmail.com"
  }
}

resource "aws_s3_bucket_versioning" "this" {
  bucket = aws_s3_bucket.this.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_ownership_controls" "this" {
  bucket = aws_s3_bucket.this.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "this" {
  bucket = aws_s3_bucket.this.id

  block_public_acls       = true
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_acl" "example" {
  depends_on = [
    aws_s3_bucket_ownership_controls.this,
    aws_s3_bucket_public_access_block.this,
  ]

  bucket = aws_s3_bucket.this.id
  acl    = "private"
}

resource "aws_s3_bucket_server_side_encryption_configuration" "example" {
  bucket = aws_s3_bucket.this.id

  rule {
    apply_server_side_encryption_by_default {
      kms_master_key_id = var.application_key_id
      sse_algorithm     = "aws:kms"
    }

    bucket_key_enabled = true
  }
}

resource "aws_ssm_parameter" "this" {
  name  = var.bucket_name_for_ssm
  type  = "String"
  value = aws_s3_bucket.this.bucket

  tags = {
    Name    = var.bucket_name_for_ssm
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}