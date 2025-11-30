#!/bin/bash

# Скрипт развёртывания FEONT на сервере
# 
# Использование:
#   ./scripts/deploy.sh <server_user>@<server_host> [jar_path]
#   Пример: ./scripts/deploy.sh user1@176.108.244.252

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

if [ $# -lt 1 ]; then
    echo "Использование: $0 <server_user>@<server_host> [jar_path]"
    echo "Пример: $0 user1@176.108.244.252"
    exit 1
fi

SERVER="$1"
JAR_PATH="${2:-$PROJECT_ROOT/backend/target/feont-1.0.0-SNAPSHOT.jar}"
REMOTE_DIR="/home/user1/feont"

echo "=== Развёртывание FEONT на сервере ==="
echo "Сервер: $SERVER"
echo "JAR файл: $JAR_PATH"
echo "Удалённая директория: $REMOTE_DIR"
echo ""

# Проверка существования JAR файла
if [ ! -f "$JAR_PATH" ]; then
    echo "Ошибка: JAR файл не найден: $JAR_PATH"
    echo "Сначала выполните сборку: ./scripts/build.sh"
    exit 1
fi

echo ">>> Подготовка структуры каталогов на сервере..."
ssh "$SERVER" "mkdir -p $REMOTE_DIR/data/tdb2 && chmod 755 $REMOTE_DIR/data/tdb2"

echo ">>> Копирование JAR файла на сервер..."
scp "$JAR_PATH" "$SERVER:$REMOTE_DIR/feont-1.0.0.jar"

echo ">>> Копирование systemd service файла (требуются права sudo)..."
ssh "$SERVER" "sudo cp $REMOTE_DIR/feont.service /etc/systemd/system/feont.service 2>/dev/null || echo 'Service файл не найден, создайте его вручную'"

echo ">>> Перезагрузка systemd..."
ssh "$SERVER" "sudo systemctl daemon-reload"

echo ""
echo "=== Развёртывание завершено ==="
echo ""
echo "Следующие шаги:"
echo "1. Создайте systemd service файл на сервере:"
echo "   sudo nano /etc/systemd/system/feont.service"
echo ""
echo "2. Включите автозапуск:"
echo "   sudo systemctl enable feont"
echo ""
echo "3. Запустите приложение:"
echo "   sudo systemctl start feont"
echo ""
echo "4. Проверьте статус:"
echo "   sudo systemctl status feont"

