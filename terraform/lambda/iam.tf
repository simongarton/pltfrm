data "aws_iam_policy" "aws_lambda_basic_execution_pltfrm_role" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

data "aws_iam_policy" "aws_ssm_read_only_access" {
  arn = "arn:aws:iam::aws:policy/AmazonSSMReadOnlyAccess"
}

resource "aws_iam_role" "pltfrm_lambda_iam" {
  name = "pltfrm-lambda-iam"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Sid       = "AssumeRolePolicy"
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
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
        Sid      = "PassRolePolicy"
        Effect   = "Allow"
        Action   = "iam:PassRole"
        Resource = "*"
      },
    ]
  })
}

resource "aws_iam_policy" "pltfrm_infrastructure_policy" {
  name = "pltfrm-s3-infrastructure-policy"

  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Sid    = "KMSPolicy",
        Effect = "Allow"
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:GenerateDataKey",
        ]
        Resource = var.application_key_arn
      },
      {
        Sid    = "CloudWatchPolicy"
        Effect = "Allow"
        Action = [
          "cloudwatch:PutMetricData",
          "cloudwatch:GetMetricData",
          "cloudwatch:GetMetricStatistics",
          "cloudwatch:ListMetrics",
        ]
        Resource = "*"
      },
    ]
  })
}

resource "aws_iam_policy" "pltfrm_s3_policy" {
  name = "pltfrm-s3-policy"

  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Sid    = "S3BucketPolicy"
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:ListBucket"
        ]
        Resource = "*"
      },
    ]
  })
}

resource "aws_iam_policy" "pltfrm_sns_sqs_policy" {
  name = "pltfrm-sns-sqs-policy"

  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Sid    = "SNSPublishPolicy"
        Effect = "Allow"
        Action = [
          "sns:Publish"
        ]
        Resource = "*"
      },
      {
        Sid    = "SQSQueuePolicy"
        Effect = "Allow"
        Action = [
          "sqs:SendMessage",
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ]
        Resource = "*"
      },
    ]
  })
}

resource "aws_iam_policy" "pltfrm_database_policy" {
  name = "pltfrm-database-policy"

  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Sid    = "TimestreamDatabasePolicy"
        Effect = "Allow"
        Action = [
          "timestream:WriteRecords",
          "timestream:Select"
        ]
        Resource = var.timestream_table_arn
      },
      {
        Sid      = "TimestreamTablePolicy"
        "Effect" = "Allow",
        "Action" = [
          "timestream:DescribeEndpoints",
          "timestream:DescribeDatabase",
          "timestream:DescribeTable",
        ],
        "Resource" : "*"
      },
      {
        Sid    = "DynamoDBPolicy"
        Effect = "Allow"
        Action = [
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query",
          "dynamodb:Scan",
          "dynamodb:DescribeTable",
        ]
        Resource = var.dynamodb_table_arns
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "aws_lambda_basic_execution_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.aws_lambda_basic_execution_pltfrm_role.arn
}

resource "aws_iam_role_policy_attachment" "aws_ssm_read_only_access_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = data.aws_iam_policy.aws_ssm_read_only_access.arn
}

resource "aws_iam_role_policy_attachment" "pltfrm_iam_pass_role_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = aws_iam_policy.pltfrm_iam_pass_role.arn
}

resource "aws_iam_role_policy_attachment" "pltfrm_infrastructure_policy_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = aws_iam_policy.pltfrm_infrastructure_policy.arn
}

resource "aws_iam_role_policy_attachment" "pltfrm_s3_policy_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = aws_iam_policy.pltfrm_s3_policy.arn
}

resource "aws_iam_role_policy_attachment" "pltfrm_sns_sqs_policy_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = aws_iam_policy.pltfrm_sns_sqs_policy.arn
}

resource "aws_iam_role_policy_attachment" "pltfrm_database_policy_attach" {
  role       = aws_iam_role.pltfrm_lambda_iam.name
  policy_arn = aws_iam_policy.pltfrm_database_policy.arn
}
