# HeatWave 규칙: 8~32자, 대소문자·숫자·특수문자 각 1개 이상
# 특수문자는 .env와 JDBC URL에서 문제없는 것만 사용
resource "random_password" "db_admin" {
  length           = 24
  min_upper        = 1
  min_lower        = 1
  min_numeric      = 1
  min_special      = 1
  override_special = "-_+"
}

# HeatWave MySQL Always Free (홈 리전에 1개, 스토리지 50GB + 백업 50GB)
resource "oci_mysql_mysql_db_system" "main" {
  compartment_id      = local.compartment_id
  availability_domain = data.oci_identity_availability_domains.ads.availability_domains[0].name
  display_name        = "halo-db"
  shape_name          = "MySQL.Free"
  subnet_id           = oci_core_subnet.private.id
  hostname_label      = "db"

  # null이면 OCI 기본 버전, 기존 RDS와 맞추려면 8.4.x로 지정
  mysql_version = var.mysql_version

  admin_username = var.db_admin_username
  admin_password = random_password.db_admin.result

  data_storage_size_in_gb = 50
  is_highly_available     = false

  backup_policy {
    is_enabled        = true
    retention_in_days = 7
  }

  deletion_policy {
    is_delete_protected = true
  }

  lifecycle {
    # 콘솔에서 비밀번호를 바꿔도 Terraform이 되돌리지 않게
    ignore_changes = [admin_password]
  }
}

output "db_private_ip" {
  description = "DB 사설 IP (GitHub Secrets의 DB_URL에 사용)"
  value       = oci_mysql_mysql_db_system.main.ip_address
}

output "db_admin_password" {
  description = "DB 관리자 비밀번호 (terraform output -raw db_admin_password로 확인)"
  value       = random_password.db_admin.result
  sensitive   = true
}
