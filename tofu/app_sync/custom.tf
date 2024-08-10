resource "aws_appsync_datasource" "insult_generator_datasource" {

  api_id           = aws_appsync_graphql_api.this.id
  name             = "InsultGeneratorDataSource"
  type             = "AWS_LAMBDA"
  service_role_arn = aws_iam_role.this.arn

  lambda_config {
    function_arn = var.insult_lambda_arn
  }
}

resource "aws_appsync_resolver" "insult_resolver" {
  api_id      = aws_appsync_graphql_api.this.id
  type        = "Query"
  field       = "getInsult"
  data_source = aws_appsync_datasource.insult_generator_datasource.name

  request_template = <<EOF
  {
    "version": "2018-05-29",
    "operation": "Invoke",
    "payload": {
      "arguments": $util.toJson($context.arguments),
      "identity": $util.toJson($context.identity),
      "source": $util.toJson($context.source),
      "request": $util.toJson($context.request),
      "prev": $util.toJson($context.prev)
    }
  }
  EOF

  response_template = <<EOF
  $util.toJson($context.result)
  EOF
}

resource "aws_appsync_resolver" "insult_string_resolver" {
  api_id      = aws_appsync_graphql_api.this.id
  type        = "Query"
  field       = "getInsultString"
  data_source = aws_appsync_datasource.insult_generator_datasource.name

  request_template = <<EOF
  {
    "version": "2018-05-29",
    "operation": "Invoke",
    "payload": {
      "arguments": $util.toJson($context.arguments),
      "identity": $util.toJson($context.identity),
      "source": $util.toJson($context.source),
      "request": $util.toJson($context.request),
      "prev": $util.toJson($context.prev)
    }
  }
  EOF

  response_template = <<EOF
  $util.toJson($context.result)
  EOF
}

resource "aws_iam_role" "this" {

  name = "insult_generator_datasource_role"

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

data "aws_iam_policy_document" "appsync_lambda_invoke" {
  statement {
    actions = [
      "lambda:InvokeFunction"
    ]

    resources = [
      var.insult_lambda_arn
    ]
  }
}

resource "aws_iam_policy" "appsync_lambda_invoke" {
  name        = "appsync_lambda_invoke"
  description = "Allows AppSync to invoke a specific Lambda function"
  policy      = data.aws_iam_policy_document.appsync_lambda_invoke.json
}

resource "aws_iam_role_policy_attachment" "appsync_lambda_invoke_attach" {
  role       = aws_iam_role.this.name
  policy_arn = aws_iam_policy.appsync_lambda_invoke.arn
}