#!/bin/bash

# Скрипт обновления FEONT на сервере
# 
# Использование:
#   ./scripts/update.sh <server_user>@<server_host> [new_jar_path]
#   Пример: ./scripts/update.sh user1@176.108.244.252

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

if [ $# -lt 1 ]; then
    echo "Использование: $0 <server_user>@<server_host> [new_jar_path]"
    echo "Пример: $0 user1@176.108.244.252"
    exit 1
fi

SERVER="$1"
JAR_PATH="${2:-$PROJECT_ROOT/backend/target/feont-1.0.0-SNAPSHOT.jar}"
REMOTE_DIR="/home/user1/feont"
SERVICE_NAME="feont"

echo "=== Обновление FEONT на сервере ==="
echo "Сервер: $SERVER"
echo "JAR файл: $JAR_PATH"
echo ""

# Проверка существования JAR файла
if [ ! -f "$JAR_PATH" ]; then
    echo "Ошибка: JAR файл не найден: $JAR_PATH"
    echo "Сначала выполните сборку: ./scripts/build.sh"
    exit 1
fi

echo ">>> Остановка приложения..."
ssh "$SERVER" "sudo systemctl stop $SERVICE_NAME || echo 'Сервис не запущен'"

echo ">>> Резервное копирование текущей версии..."
ssh "$SERVER" "cd $REMOTE_DIR && \
    if [ -f feont-1.0.0.jar ]; then \
        cp feont-1.0.0.jar feont-1.0.0.jar.backup-\$(date +%Y%m%d-%H%M%S); \
        echo 'Резервная копия создана'; \
    fi"

echo ">>> Копирование нового JAR файла..."
scp "$JAR_PATH" "$SERVER:$REMOTE_DIR/feont-1.0.0.jar"

echo ">>> Запуск приложения..."
ssh "$SERVER" "sudo systemctl start $SERVICE_NAME"

echo ">>> Ожидание запуска (10 секунд)..."
sleep 10

echo ">>> Проверка статуса..."
ssh "$SERVER" "sudo systemctl status $SERVICE_NAME --no-pager -l | head -20"

echo ""
echo "=== Обновление завершено ==="
echo ""
echo "Проверьте логи:"
echo "  sudo journalctl -u $SERVICE_NAME -f"
echo ""
echo "Если что-то пошло не так, откатитесь:"
echo "  ssh $SERVER \"cd $REMOTE_DIR && mv feont-1.0.0.jar.backup-* feont-1.0.0.jar && sudo systemctl restart $SERVICE_NAME\""

