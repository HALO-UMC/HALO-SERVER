variable "region" {
  description = "OCI 리전 (홈 리전과 같아야 Always Free 적용)"
  type        = string
  default     = "ap-osaka-1"
}

variable "oci_profile" {
  description = "~/.oci/config에서 사용할 프로필 이름"
  type        = string
  default     = "DEFAULT"
}

variable "tenancy_ocid" {
  description = "tenancy OCID (가용 도메인 조회용)"
  type        = string
}

variable "compartment_ocid" {
  description = "리소스를 만들 compartment OCID, null이면 루트(tenancy)에 생성"
  type        = string
  default     = null
}

variable "admin_ssh_cidr" {
  description = "SSH 22번을 임시로 열 CIDR (예: 내 IP/32), null이면 닫힘"
  type        = string
  default     = null
}

variable "ssh_authorized_keys" {
  description = "ubuntu 계정에 등록할 SSH 공개키 (내 키와 CD용 키, 줄바꿈으로 구분)"
  type        = string
}

variable "app_dir" {
  description = "CD가 배포할 디렉터리, GitHub Secrets의 EC2_APP_DIR과 같아야 함"
  type        = string
  default     = "/home/ubuntu/halo"
}

variable "tailscale_auth_key" {
  description = "서버를 tailnet에 등록할 Tailscale 인증 키 (1회용, tag:server)"
  type        = string
  sensitive   = true
}

variable "mysql_version" {
  description = "HeatWave MySQL 버전 (예: 8.4.6), null이면 OCI 기본값"
  type        = string
  default     = null
}

variable "db_admin_username" {
  description = "HeatWave 관리자 계정 이름"
  type        = string
  default     = "admin"
}
