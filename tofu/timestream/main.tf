resource "aws_timestreamwrite_database" "this" {
  database_name = var.database_name

  tags = {
    Name    = var.database_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltform"
  }
}

resource "aws_timestreamwrite_table" "day_table" {

  database_name = aws_timestreamwrite_database.this.database_name
  table_name    = var.day_table_name

  retention_properties {
    magnetic_store_retention_period_in_days = 1000
    memory_store_retention_period_in_hours  = 58
  }

  tags = {
    Name    = var.day_table_name
    Owner   = "simon.garton@gmail.com"
    Project = "pltform"
  }
}

resource "aws_ssm_parameter" "this" {
  name  = var.database_name_for_ssm
  type  = "String"
  value = aws_timestreamwrite_database.this.database_name

  tags = {
    Name    = var.database_name_for_ssm
    Owner   = "simon.garton@gmail.com"
    Project = "pltfrm"
  }
}

