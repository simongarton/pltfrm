terraform {

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.53.0"
    }
  }
  backend "s3" {
    encrypt        = true
    bucket         = "pltfrm-terraform-remote-state-ap-southeast-2"
    dynamodb_table = "pltfrm-terraform-remote-state-ap-southeast-2"
    region         = "ap-southeast-2"
    key            = "terraform.tfstate"
    profile        = "Admin"
  }
}