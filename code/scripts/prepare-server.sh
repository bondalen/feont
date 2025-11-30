#!/bin/bash

# Скрипт подготовки сервера для развёртывания FEONT
# 
# Использование:
#   ./scripts/prepare-server.sh <server_user>@<server_host>
#   Пример: ./scripts/prepare-server.sh user1@176.108.244.252

set -e

if [ $# -lt 1 ]; then
    echo "Использование: $0 <server_user>@<server_host>"
    echo "Пример: $0 user1@176.108.244.252"
    exit 1
fi

SERVER="$1"
REMOTE_DIR="/home/user1/feont"

echo "=== Подготовка сервера для FEONT ==="
echo "Сервер: $SERVER"
echo ""

echo ">>> Создание структуры каталогов..."
ssh "$SERVER" << EOF
mkdir -p $REMOTE_DIR/data/tdb2
mkdir -p $REMOTE_DIR/logs
mkdir -p $REMOTE_DIR/config
chmod 755 $REMOTE_DIR/data/tdb2
chmod 755 $REMOTE_DIR/logs
chmod 755 $REMOTE_DIR/config

echo "Структура каталогов создана:"
ls -la $REMOTE_DIR/
EOF

echo ""
echo ">>> Проверка Java..."
ssh "$SERVER" "java -version || echo 'Java не найден'"

echo ""
echo ">>> Проверка места на диске..."
ssh "$SERVER" "df -h $REMOTE_DIR"

echo ""
echo "=== Подготовка сервера завершена ==="
echo "Директории созданы:"
echo "  - $REMOTE_DIR/data/tdb2/ (TDB2 хранилище)"
echo "  - $REMOTE_DIR/logs/ (логи приложения)"
echo "  - $REMOTE_DIR/config/ (конфигурационные файлы)"

