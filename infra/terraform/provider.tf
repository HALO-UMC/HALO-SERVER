# 인증은 ~/.oci/config의 프로필(API 키)을 사용
provider "oci" {
  region              = var.region
  config_file_profile = var.oci_profile
}
