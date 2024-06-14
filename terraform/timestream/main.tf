resource "aws_timestreamwrite_database" "this" {
  database_name = var.database_name

  tags = {
    Name    = var.database_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltform"
  }
}


resource "aws_timestreamwrite_table" "readings-table" {

  for_each      = toset(var.table_names)
  database_name = aws_timestreamwrite_database.this.database_name
  table_name    = each.key

  retention_properties {
    magnetic_store_retention_period_in_days = 1000
    memory_store_retention_period_in_hours  = 58
  }

  tags = {
    Name    = each.key
    Owner   = "simon.garton@gmail.com"
    Project = "pltform"
  }
}