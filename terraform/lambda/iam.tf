data "aws_iam_policy" "aws_lambda_basic_execution_pltfrm_role" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

data "aws_iam_policy" "aws_lambda_vpc_access_execution_pltfrm_role" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

data "aws_iam_policy" "s3_full_access_pltfrm_role" {
  arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

data "aws_iam_policy" "lambda_full_access_pltfrm_role" {
  arn = "arn:aws:iam::aws:policy/AWSLambda_FullAccess"
}

data "aws_iam_policy" "sqs_full_access_pltfrm_role" {
  arn = "arn:aws:iam::aws:policy/AmazonSQSFullAccess"
}

data "aws_iam_policy" "sns_full_access_pltfrm_role" {
  arn = "arn:aws:iam::aws:policy/AmazonSNSFullAccess"
}

data "aws_iam_policy" "ssm_full_access_pltfrm_role" {
  arn = "arn:aws:iam::aws:policy/AmazonSSMFullAccess"
}

data "aws_iam_policy" "dynamodb-full-access-lambda-queue-role" {
  arn = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
}

resource "aws_iam_role" "pltfrm_lambda_iam" {
  name = "pltfrm_lambda-iam"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Sid       = ""
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      },
    ]
  })
}

resource "aws_iam_policy" "pltfrm_iam_pass_role" {
  name = "pltfrm-iam-pass-role"

  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = "iam:PassRole"
        Resource = "*"
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "aws_lambda_vpc_access_execution_pltfrm_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.aws_lambda_vpc_access_execution_pltfrm_role.arn
}

resource "aws_iam_role_policy_attachment" "s3_full_access_role_pltfrm_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.s3_full_access_pltfrm_role.arn
}

resource "aws_iam_role_policy_attachment" "lambda_full_access_role_pltfrm_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.lambda_full_access_pltfrm_role.arn
}

resource "aws_iam_role_policy_attachment" "sqs_full_access_role_pltfrm_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.sqs_full_access_pltfrm_role.arn
}

resource "aws_iam_role_policy_attachment" "sns_full_access_role_pltfrm_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.sns_full_access_pltfrm_role.arn
}

resource "aws_iam_role_policy_attachment" "ssm_full_access_role_pltfrm_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.ssm_full_access_pltfrm_role.arn
}

resource "aws_iam_role_policy_attachment" "iam_pass_role_pltfrm_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = aws_iam_policy.pltfrm_iam_pass_role.arn
}

resource "aws_iam_role_policy_attachment" "dynamodb-full-access-role-lambda-queue-attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.dynamodb-full-access-lambda-queue-role.arn
}
