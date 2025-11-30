#!/bin/bash

# Полный скрипт сборки и развёртывания FEONT
# 
# Использование:
#   ./scripts/full-deploy.sh <server_user>@<server_host> [--no-build] [--no-test]
#   Пример: ./scripts/full-deploy.sh user1@176.108.244.252

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

NO_BUILD=false
NO_TEST=false

# Парсинг аргументов
SERVER=""
for arg in "$@"; do
    case $arg in
        --no-build)
            NO_BUILD=true
            shift
            ;;
        --no-test)
            NO_TEST=true
            shift
            ;;
        *)
            if [ -z "$SERVER" ] && [[ $arg == *@* ]]; then
                SERVER="$arg"
            fi
            shift
            ;;
    esac
done

if [ -z "$SERVER" ]; then
    echo "Использование: $0 <server_user>@<server_host> [--no-build] [--no-test]"
    echo "Пример: $0 user1@176.108.244.252"
    echo ""
    echo "Опции:"
    echo "  --no-build  Пропустить сборку (использовать существующий JAR)"
    echo "  --no-test   Пропустить локальное тестирование"
    exit 1
fi

REMOTE_DIR="/home/user1/feont"
JAR_FILE="$PROJECT_ROOT/backend/target/feont-1.0.0-SNAPSHOT.jar"

echo "=== Полное развёртывание FEONT ==="
echo "Сервер: $SERVER"
echo "Пропуск сборки: $NO_BUILD"
echo "Пропуск тестирования: $NO_TEST"
echo ""

# Шаг 1: Подготовка сервера
echo ">>> Шаг 1: Подготовка сервера..."
"$SCRIPT_DIR/prepare-server.sh" "$SERVER"

# Шаг 2: Сборка (если не пропущена)
if [ "$NO_BUILD" = false ]; then
    echo ""
    echo ">>> Шаг 2: Сборка проекта..."
    "$SCRIPT_DIR/build.sh"
else
    echo ""
    echo ">>> Шаг 2: Сборка пропущена (--no-build)"
    if [ ! -f "$JAR_FILE" ]; then
        echo "Ошибка: JAR файл не найден: $JAR_FILE"
        echo "Выполните сборку: ./scripts/build.sh"
        exit 1
    fi
fi

# Шаг 3: Локальное тестирование (если не пропущено)
if [ "$NO_TEST" = false ]; then
    echo ""
    echo ">>> Шаг 3: Локальное тестирование..."
    echo "Примечание: Для полного тестирования используйте: ./scripts/test-local.sh"
    echo "Проверка JAR файла..."
    if [ -f "$JAR_FILE" ]; then
        JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
        echo "✓ JAR файл найден: $JAR_FILE ($JAR_SIZE)"
    else
        echo "✗ JAR файл не найден!"
        exit 1
    fi
else
    echo ""
    echo ">>> Шаг 3: Тестирование пропущено (--no-test)"
fi

# Шаг 4: Настройка systemd (если не настроен)
echo ""
echo ">>> Шаг 4: Проверка systemd service..."
ssh "$SERVER" "sudo systemctl cat feont.service > /dev/null 2>&1" || {
    echo "Systemd service не настроен. Настраиваю..."
    "$SCRIPT_DIR/setup-systemd.sh" "$SERVER"
}

# Шаг 5: Настройка Nginx (если не настроен)
echo ""
echo ">>> Шаг 5: Проверка Nginx конфигурации..."
ssh "$SERVER" "test -f /etc/nginx/sites-available/feont" || {
    echo "Nginx конфигурация не найдена. Настраиваю..."
    "$SCRIPT_DIR/setup-nginx.sh" "$SERVER"
}

# Шаг 6: Развёртывание
echo ""
echo ">>> Шаг 6: Развёртывание приложения..."
"$SCRIPT_DIR/deploy.sh" "$SERVER" "$JAR_FILE"

# Шаг 7: Запуск приложения
echo ""
echo ">>> Шаг 7: Запуск приложения..."
ssh "$SERVER" "sudo systemctl start feont" || {
    echo "Предупреждение: Не удалось запустить приложение автоматически."
    echo "Запустите вручную: ssh $SERVER 'sudo systemctl start feont'"
}

# Ожидание запуска
echo ""
echo ">>> Ожидание запуска приложения (10 секунд)..."
sleep 10

# Проверка статуса
echo ""
echo ">>> Проверка статуса..."
ssh "$SERVER" "sudo systemctl status feont --no-pager -l | head -20"

echo ""
echo "=== Развёртывание завершено! ==="
echo ""
echo "Проверьте приложение:"
echo "  - Health: http://$SERVER/health (через Nginx)"
echo "  - SPARQL: http://$SERVER/ds/ping"
echo "  - Frontend: http://$SERVER/"
echo ""
echo "Логи:"
echo "  ssh $SERVER 'sudo journalctl -u feont -f'"
echo ""
echo "Обновление в будущем:"
echo "  ./scripts/update.sh $SERVER"

