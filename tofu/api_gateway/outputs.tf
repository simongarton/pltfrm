output "api" {
  value = aws_api_gateway_rest_api.api
}

output "key" {
  value = aws_api_gateway_api_key.pltfrm_api_key
}