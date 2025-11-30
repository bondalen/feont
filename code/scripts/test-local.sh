#!/bin/bash

# Скрипт локального тестирования FEONT
# 
# Использование:
#   ./scripts/test-local.sh [jar_path]
#   Пример: ./scripts/test-local.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

JAR_PATH="${1:-$PROJECT_ROOT/backend/target/feont-1.0.0-SNAPSHOT.jar}"
TEST_DATA_DIR="$PROJECT_ROOT/data/tdb2"
PORT=8083

echo "=== Локальное тестирование FEONT ==="
echo "JAR файл: $JAR_PATH"
echo "Порт: $PORT"
echo ""

# Проверка существования JAR файла
if [ ! -f "$JAR_PATH" ]; then
    echo "Ошибка: JAR файл не найден: $JAR_PATH"
    echo "Сначала выполните сборку: ./scripts/build.sh"
    exit 1
fi

# Создание директории для тестовых данных
mkdir -p "$TEST_DATA_DIR"

# Проверка доступности порта
if lsof -Pi :$PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "Предупреждение: Порт $PORT уже занят!"
    echo "Остановите другой процесс или измените порт."
    read -p "Продолжить? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ">>> Запуск приложения..."
echo "Команда: java -jar $JAR_PATH --spring.profiles.active=dev --feont.tdb2.path=$TEST_DATA_DIR --server.port=$PORT"
echo ""
echo "Приложение запущено. Откройте в браузере: http://localhost:$PORT"
echo ""
echo "Проверьте endpoints:"
echo "  - Health check: http://localhost:$PORT/health"
echo "  - SPARQL ping: http://localhost:$PORT/ds/ping"
echo "  - Frontend: http://localhost:$PORT/"
echo ""
echo "Нажмите Ctrl+C для остановки приложения"
echo ""

# Запуск приложения в фоновом режиме для тестирования
java -jar "$JAR_PATH" \
    --spring.profiles.active=dev \
    --feont.tdb2.path="$TEST_DATA_DIR" \
    --server.port=$PORT &
    
APP_PID=$!

# Функция очистки при выходе
cleanup() {
    echo ""
    echo ">>> Остановка приложения (PID: $APP_PID)..."
    kill $APP_PID 2>/dev/null || true
    wait $APP_PID 2>/dev/null || true
    echo "Приложение остановлено."
}

trap cleanup EXIT INT TERM

# Ожидание запуска
echo "Ожидание запуска приложения..."
sleep 5

# Проверка доступности
echo ">>> Проверка доступности..."
if curl -s -f "http://localhost:$PORT/health" > /dev/null 2>&1; then
    echo "✓ Health check: OK"
else
    echo "✗ Health check: FAILED"
fi

if curl -s -f "http://localhost:$PORT/ds/ping" > /dev/null 2>&1; then
    echo "✓ SPARQL endpoint: OK"
else
    echo "✗ SPARQL endpoint: FAILED"
fi

echo ""
echo "Приложение работает. Нажмите Ctrl+C для остановки."
echo "Логи приложения отображаются выше."

# Ожидание завершения
wait $APP_PID

