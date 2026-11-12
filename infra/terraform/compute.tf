data "oci_identity_availability_domains" "ads" {
  compartment_id = var.tenancy_ocid
}

# 최신 Ubuntu 24.04 aarch64 이미지
data "oci_core_images" "ubuntu" {
  compartment_id           = local.compartment_id
  operating_system         = "Canonical Ubuntu"
  operating_system_version = "24.04"
  shape                    = "VM.Standard.A1.Flex"
  sort_by                  = "TIMECREATED"
  sort_order               = "DESC"
}

# Always Free 한도: A1 합계 2 OCPU / 12GB (넘기면 과금 또는 회수)
resource "oci_core_instance" "app" {
  compartment_id      = local.compartment_id
  availability_domain = data.oci_identity_availability_domains.ads.availability_domains[0].name
  display_name        = "halo-prod"
  shape               = "VM.Standard.A1.Flex"

  shape_config {
    ocpus         = 2
    memory_in_gbs = 12
  }

  source_details {
    source_type = "image"
    source_id   = data.oci_core_images.ubuntu.images[0].id
  }

  create_vnic_details {
    subnet_id        = oci_core_subnet.public.id
    assign_public_ip = true
    hostname_label   = "app"
  }

  is_pv_encryption_in_transit_enabled = true

  metadata = {
    ssh_authorized_keys = var.ssh_authorized_keys
    user_data = base64encode(templatefile("${path.module}/cloud-init.yaml.tftpl", {
      init_server_b64    = filebase64("${path.module}/../../deploy/init-server.sh")
      app_dir            = var.app_dir
      tailscale_auth_key = var.tailscale_auth_key
    }))
  }

  lifecycle {
    # 새 이미지 출시나 cloud-init 수정으로 서버가 재생성되지 않게 고정 (cloud-init은 최초 부팅 때만 실행)
    ignore_changes = [source_details[0].source_id, metadata]
  }
}

output "app_public_ip" {
  description = "앱 서버 공인 IP (DNS A 레코드에 지정)"
  value       = oci_core_instance.app.public_ip
}
