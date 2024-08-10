resource "aws_iam_role" "appsync_dynamodb_role" {
  name = "AppSyncDynamoDBRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "appsync.amazonaws.com"
        }
      },
    ],
  })
}

resource "aws_iam_policy" "appsync_dynamodb_policy" {

  name = "AppSyncDynamoDBPolicy"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query",
          "dynamodb:Scan"
        ],
        Resource = "arn:aws:dynamodb:${var.region}:${var.account_id}:table/${var.table_name}"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "appsync_dynamodb_attach" {
  role       = aws_iam_role.appsync_dynamodb_role.name
  policy_arn = aws_iam_policy.appsync_dynamodb_policy.arn
}
