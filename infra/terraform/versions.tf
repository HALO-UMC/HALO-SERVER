terraform {
  # backend "oci"는 Terraform 1.12부터 지원
  required_version = ">= 1.12"

  required_providers {
    oci = {
      source  = "oracle/oci"
      version = "~> 7.0"
    }
  }

  # state는 OCI Object Storage 버킷에 저장 (값은 backend.hcl로 주입)
  # terraform init -backend-config=backend.hcl
  backend "oci" {}
}
