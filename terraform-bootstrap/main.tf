resource "aws_dynamodb_table" "terraform_state_lock" {
  name           = "pltfrm-terraform-remote-state-ap-southeast-2"
  billing_mode = "PAY_PER_REQUEST"

  hash_key       = "LockID"
  attribute {
    name = "LockID"
    type = "S"
  }
}

resource "aws_s3_bucket" "terraform_bucket" {
  bucket = "pltfrm-terraform-remote-state-ap-southeast-2"
}