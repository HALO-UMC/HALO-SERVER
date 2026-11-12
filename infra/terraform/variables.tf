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

variable "compartment_ocid" {
  description = "리소스를 만들 compartment OCID (루트면 tenancy OCID)"
  type        = string
}

variable "admin_ssh_cidr" {
  description = "SSH 22번을 임시로 열 CIDR (예: 내 IP/32), null이면 닫힘"
  type        = string
  default     = null
}
