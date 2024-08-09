output "database" {
  value = aws_timestreamwrite_database.this
}

output "day_table" {
  value = aws_timestreamwrite_table.day_table
}
