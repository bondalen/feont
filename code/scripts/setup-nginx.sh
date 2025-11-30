#!/bin/bash

# Скрипт настройки Nginx для FEONT
# 
# Использование:
#   ./scripts/setup-nginx.sh <server_user>@<server_host>
#   Пример: ./scripts/setup-nginx.sh user1@176.108.244.252

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ $# -lt 1 ]; then
    echo "Использование: $0 <server_user>@<server_host>"
    echo "Пример: $0 user1@176.108.244.252"
    exit 1
fi

SERVER="$1"
CONF_FILE="$SCRIPT_DIR/nginx-feont.conf"
REMOTE_CONF="/etc/nginx/sites-available/feont"
REMOTE_ENABLED="/etc/nginx/sites-enabled/feont"

echo "=== Настройка Nginx для FEONT ==="
echo "Сервер: $SERVER"
echo ""

if [ ! -f "$CONF_FILE" ]; then
    echo "Ошибка: Файл конфигурации не найден: $CONF_FILE"
    exit 1
fi

echo ">>> Копирование конфигурации на сервер..."
scp "$CONF_FILE" "$SERVER:/tmp/feont.conf"

echo ">>> Установка конфигурации (требуются права sudo)..."
ssh "$SERVER" << EOF
sudo mv /tmp/feont.conf $REMOTE_CONF
sudo chmod 644 $REMOTE_CONF

# Создание символической ссылки в sites-enabled
if [ ! -L $REMOTE_ENABLED ]; then
    sudo ln -s $REMOTE_CONF $REMOTE_ENABLED
    echo "Конфигурация активирована"
else
    echo "Конфигурация уже активирована"
fi

# Проверка конфигурации
echo ">>> Проверка конфигурации Nginx..."
sudo nginx -t

if [ \$? -eq 0 ]; then
    echo ">>> Перезагрузка Nginx..."
    sudo systemctl reload nginx
    echo "Nginx настроен успешно"
else
    echo "Ошибка в конфигурации Nginx. Проверьте файл: $REMOTE_CONF"
    exit 1
fi
EOF

echo ""
echo "=== Настройка Nginx завершена ==="
echo "Конфигурация установлена: $REMOTE_CONF"
echo "Активирована: $REMOTE_ENABLED"

