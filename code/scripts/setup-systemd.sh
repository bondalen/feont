#!/bin/bash

# Скрипт настройки systemd service для FEONT
# 
# Использование:
#   ./scripts/setup-systemd.sh <server_user>@<server_host>
#   Пример: ./scripts/setup-systemd.sh user1@176.108.244.252

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ $# -lt 1 ]; then
    echo "Использование: $0 <server_user>@<server_host>"
    echo "Пример: $0 user1@176.108.244.252"
    exit 1
fi

SERVER="$1"
SERVICE_FILE="$SCRIPT_DIR/feont.service"
REMOTE_SERVICE="/etc/systemd/system/feont.service"
SERVICE_NAME="feont"

echo "=== Настройка systemd service для FEONT ==="
echo "Сервер: $SERVER"
echo ""

if [ ! -f "$SERVICE_FILE" ]; then
    echo "Ошибка: Файл service не найден: $SERVICE_FILE"
    exit 1
fi

echo ">>> Копирование service файла на сервер..."
scp "$SERVICE_FILE" "$SERVER:/tmp/feont.service"

echo ">>> Установка service файла (требуются права sudo)..."
ssh "$SERVER" << EOF
sudo mv /tmp/feont.service $REMOTE_SERVICE
sudo chmod 644 $REMOTE_SERVICE

echo ">>> Перезагрузка systemd..."
sudo systemctl daemon-reload

echo ">>> Включение автозапуска..."
sudo systemctl enable $SERVICE_NAME

echo ">>> Проверка конфигурации..."
sudo systemctl cat $SERVICE_NAME | head -20

echo ""
echo "=== Service настроен ==="
echo "Запуск: sudo systemctl start $SERVICE_NAME"
echo "Остановка: sudo systemctl stop $SERVICE_NAME"
echo "Статус: sudo systemctl status $SERVICE_NAME"
echo "Логи: sudo journalctl -u $SERVICE_NAME -f"
EOF

echo ""
echo "=== Настройка systemd service завершена ==="

