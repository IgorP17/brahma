#!/usr/bin/env bash
#
# zero_compress.sh — подготовка VM к бекапу с минимизацией 7zip архива
# Запускать ВНУТРИ виртуалки (не по SSH!).
#
# Использование:
#   sudo ./zero_compress.sh
#
# Скрипт:
#   1. Удаляет старые Docker-объекты (dangling-образы, stopped-контейнеры, volumes)
#   2. Очищает логи Jenkins (старые билды)
#   3. Очищает системные логи
#   4. Очищает временные файлы
#   5. Заполняет свободное место нулями
#
# ВАЖНО: не удаляем Docker-образы, Maven-кэш и workspace —
# при восстановлении из бекапа VM должна работать сразу без пересборки.

set -euo pipefail

# ======================== Цвета ========================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log()  { echo -e "${GREEN}[+]${NC} $1"; }
warn() { echo -e "${YELLOW}[!]${NC} $1"; }

# ======================== Проверка прав ========================
[ "$EUID" -eq 0 ] || err "Этот скрипт требует sudo. Запусти: sudo $0"

err() { echo -e "${RED}[-]${NC} $1"; exit 1; }

# ======================== 1. Очистка старых Docker-объектов ========================
log "=== 1. Очистка старых Docker-объектов ==="

# Удаляем dangling-образы (безымянные, не привязанные к контейнерам)
docker image prune -f 2>/dev/null || warn "Docker image prune не удался"

# Удаляем остановленные контейнеры
docker container prune -f 2>/dev/null || warn "Docker container prune не удался"

# Удаляем неиспользуемые volumes
docker volume prune -f 2>/dev/null || warn "Docker volume prune не удался"

log "Docker-объекты очищены."

# ======================== 2. Очистка логов Jenkins ========================
log "=== 2. Очистка логов Jenkins ==="

# Удаляем логи старых билдов (старше 7 дней)
find /var/lib/jenkins/jobs/*/builds/ -name "log" -mtime +7 -delete 2>/dev/null || true

# Очищаем логи самого Jenkins
truncate -s 0 /var/lib/jenkins/jenkins.model.JenkinsLocationConfiguration.xml 2>/dev/null || true

log "Логи Jenkins очищены."

# ======================== 3. Очистка системных логов ========================
log "=== 3. Очистка системных логов ==="

find /var/log -type f -name "*.log" -exec truncate -s 0 {} \; 2>/dev/null || true
find /var/log -type f -name "*.gz" -delete 2>/dev/null || true
journalctl --vacuum-time=1s 2>/dev/null || true

log "Логи очищены."

# ======================== 4. Очистка временных файлов ========================
log "=== 4. Очистка временных файлов ==="

rm -rf /tmp/* 2>/dev/null || true
rm -rf /var/tmp/* 2>/dev/null || true
rm -rf /root/.cache/pip 2>/dev/null || true

log "Временные файлы очищены."

# ======================== 5. Заполнение нулями ========================
log "=== 5. Заполнение свободного места нулями ==="
log "Это может занять много времени. Прогресс будет отображаться."
log "Будет создан файл /zero_file, который заполнит весь диск."

log "Создаю /zero_file..."
dd if=/dev/zero of=/zero_file bs=4M status=progress || true

log "Удаляю /zero_file..."
rm -f /zero_file

log "Свободное место заполнено нулями."

# ======================== 6. Итоговая инструкция ========================
echo ""
echo -e "${GREEN}============================${NC}"
echo -e "${GREEN}  Подготовка завершена!${NC}"
echo -e "${GREEN}============================${NC}"
echo ""
log "Выключи виртуалку:"
echo "  sudo shutdown -h now"
echo ""
log "Затем заархивируй диск VM с максимальной компрессией:"
echo "  7z a -t7z -m0=lzma2 -mx=9 -mfb=64 -md=32m -ms=on vm_backup.7z /path/to/vm/disk.vdi"
echo ""
