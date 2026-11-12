#!/usr/bin/env bash
# 새 서버에서 최초 1회 실행하는 초기 세팅 스크립트 (CD가 바로 배포할 수 있는 상태로 만듦)
# 지원 OS: Ubuntu, Amazon Linux 2023 / 지원 아키텍처: x86_64, aarch64
#
# 사용법: sudo APP_DIR=/home/ubuntu/halo bash init-server.sh
#   APP_DIR        (필수) CD가 배포할 디렉터리, GitHub Secrets의 EC2_APP_DIR과 동일하게
#   DEPLOY_USER    (선택) CD가 SSH로 접속할 계정, 기본값은 sudo를 실행한 계정
#   AUTHORIZED_KEY (선택) CD용 SSH 공개키, 지정하면 DEPLOY_USER의 authorized_keys에 추가
#   SWAP_SIZE      (선택) 스왑이 없을 때 만들 크기, 기본값 2G
#
# 여러 번 실행해도 이미 된 작업은 건너뜀
set -euo pipefail

COMPOSE_VERSION=v5.6.1
BUILDX_VERSION=v0.38.1

: "${APP_DIR:?APP_DIR가 필요합니다 (EC2_APP_DIR 시크릿과 같은 경로)}"
DEPLOY_USER=${DEPLOY_USER:-${SUDO_USER:-}}
SWAP_SIZE=${SWAP_SIZE:-2G}

if [ "$(id -u)" -ne 0 ]; then
  echo "root 권한이 필요합니다. sudo로 실행하세요." >&2
  exit 1
fi
if [ -z "$DEPLOY_USER" ] || ! id "$DEPLOY_USER" >/dev/null 2>&1; then
  echo "DEPLOY_USER를 확인할 수 없습니다. DEPLOY_USER=<계정>으로 지정하세요." >&2
  exit 1
fi

. /etc/os-release

install_docker_ubuntu() {
  # Docker 공식 apt 저장소 사용 (compose, buildx 플러그인 포함)
  apt-get update -y
  apt-get install -y ca-certificates curl
  install -m 0755 -d /etc/apt/keyrings
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
  chmod a+r /etc/apt/keyrings/docker.asc
  echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu ${VERSION_CODENAME} stable" \
    > /etc/apt/sources.list.d/docker.list
  apt-get update -y
  apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
}

install_docker_al2023() {
  # AL2023 기본 docker 패키지엔 compose/buildx 플러그인이 없어 공식 릴리스 바이너리를 직접 설치
  dnf install -y docker
  local plugin_dir=/usr/local/lib/docker/cli-plugins
  local compose_arch buildx_arch
  case "$(uname -m)" in
    x86_64)  compose_arch=x86_64;  buildx_arch=amd64 ;;
    aarch64) compose_arch=aarch64; buildx_arch=arm64 ;;
    *) echo "지원하지 않는 아키텍처: $(uname -m)" >&2; exit 1 ;;
  esac
  mkdir -p "$plugin_dir"
  curl -fsSL -o "$plugin_dir/docker-compose" \
    "https://github.com/docker/compose/releases/download/${COMPOSE_VERSION}/docker-compose-linux-${compose_arch}"
  curl -fsSL -o "$plugin_dir/docker-buildx" \
    "https://github.com/docker/buildx/releases/download/${BUILDX_VERSION}/buildx-${BUILDX_VERSION}.linux-${buildx_arch}"
  chmod +x "$plugin_dir/docker-compose" "$plugin_dir/docker-buildx"
}

echo "### 1. Docker 설치 ###"
if docker compose version >/dev/null 2>&1 && docker buildx version >/dev/null 2>&1; then
  echo "Docker/compose/buildx 이미 설치됨, 건너뜀"
else
  case "$ID" in
    ubuntu) install_docker_ubuntu ;;
    amzn)   install_docker_al2023 ;;
    *) echo "지원하지 않는 OS: $ID (Ubuntu, Amazon Linux 2023만 지원)" >&2; exit 1 ;;
  esac
fi
systemctl enable --now docker
usermod -aG docker "$DEPLOY_USER"

echo "### 2. 스왑 설정 ###"
if [ -n "$(swapon --show --noheadings)" ]; then
  echo "스왑 이미 있음, 건너뜀"
else
  fallocate -l "$SWAP_SIZE" /swapfile
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
  grep -q '^/swapfile ' /etc/fstab || echo '/swapfile none swap sw 0 0' >> /etc/fstab
fi

echo "### 3. 시간대 설정 ###"
timedatectl set-timezone Asia/Seoul

echo "### 4. 방화벽(iptables) 80/443 허용 ###"
# OCI Ubuntu 이미지는 iptables에 22번 외 인바운드를 막는 REJECT 규칙이 기본으로 들어 있음
# (클라우드 보안그룹/Security List와 별개로 OS 단에서도 열어야 함)
if command -v netfilter-persistent >/dev/null 2>&1; then
  for port in 80 443; do
    iptables -C INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null \
      || iptables -I INPUT 1 -p tcp --dport "$port" -j ACCEPT
  done
  netfilter-persistent save
else
  echo "netfilter-persistent 없음, 건너뜀"
fi

echo "### 5. 배포 디렉터리 / SSH 키 ###"
DEPLOY_HOME=$(getent passwd "$DEPLOY_USER" | cut -d: -f6)
install -d -o "$DEPLOY_USER" -g "$(id -gn "$DEPLOY_USER")" "$APP_DIR"
if [ -n "${AUTHORIZED_KEY:-}" ]; then
  install -d -m 700 -o "$DEPLOY_USER" -g "$(id -gn "$DEPLOY_USER")" "$DEPLOY_HOME/.ssh"
  touch "$DEPLOY_HOME/.ssh/authorized_keys"
  grep -qxF "$AUTHORIZED_KEY" "$DEPLOY_HOME/.ssh/authorized_keys" \
    || echo "$AUTHORIZED_KEY" >> "$DEPLOY_HOME/.ssh/authorized_keys"
  chown "$DEPLOY_USER":"$(id -gn "$DEPLOY_USER")" "$DEPLOY_HOME/.ssh/authorized_keys"
  chmod 600 "$DEPLOY_HOME/.ssh/authorized_keys"
fi

echo
echo "완료. 다음 단계:"
echo "  1) DOMAIN의 DNS A 레코드를 이 서버 공인 IP로 지정"
echo "  2) GitHub Secrets 갱신: EC2_HOST, EC2_USERNAME=$DEPLOY_USER, EC2_APP_DIR=$APP_DIR"
echo "     EC2_KNOWN_HOSTS는 로컬에서 'ssh-keyscan <서버IP>' 결과로 설정"
echo "  3) main에 배포 (최초 배포 시 인증서 자동 발급)"
echo "docker 그룹 반영을 위해 $DEPLOY_USER 계정은 재로그인이 필요합니다."
