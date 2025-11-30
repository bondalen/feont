#!/bin/bash

# Скрипт проверки результатов сборки
# 
# Использование:
#   ./scripts/check-build.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

JAR_FILE="$PROJECT_ROOT/backend/target/feont-1.0.0-SNAPSHOT.jar"
FRONTEND_DIR="$PROJECT_ROOT/frontend/dist/spa"
STATIC_DIR="$PROJECT_ROOT/backend/src/main/resources/static"

echo "=== Проверка результатов сборки FEONT ==="
echo ""

# Проверка JAR файла
echo ">>> Проверка JAR файла..."
if [ -f "$JAR_FILE" ]; then
    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
    JAR_SIZE_BYTES=$(stat -f%z "$JAR_FILE" 2>/dev/null || stat -c%s "$JAR_FILE" 2>/dev/null)
    echo "✓ JAR файл найден: $JAR_FILE"
    echo "  Размер: $JAR_SIZE ($JAR_SIZE_BYTES байт)"
    
    # Проверка размера (ожидается 50-100MB)
    if [ "$JAR_SIZE_BYTES" -lt 52428800 ]; then
        echo "  ⚠ Предупреждение: JAR файл меньше ожидаемого (менее 50MB)"
    elif [ "$JAR_SIZE_BYTES" -gt 104857600 ]; then
        echo "  ⚠ Предупреждение: JAR файл больше ожидаемого (более 100MB)"
    else
        echo "  ✓ Размер в пределах ожидаемого диапазона (50-100MB)"
    fi
else
    echo "✗ JAR файл не найден: $JAR_FILE"
    echo "  Выполните сборку: ./scripts/build.sh"
    exit 1
fi

# Проверка frontend сборки
echo ""
echo ">>> Проверка frontend сборки..."
if [ -d "$FRONTEND_DIR" ]; then
    FILE_COUNT=$(find "$FRONTEND_DIR" -type f | wc -l)
    echo "✓ Frontend собран: $FRONTEND_DIR"
    echo "  Файлов: $FILE_COUNT"
    
    # Проверка index.html
    if [ -f "$FRONTEND_DIR/index.html" ]; then
        echo "  ✓ index.html найден"
    else
        echo "  ✗ index.html не найден!"
    fi
else
    echo "✗ Frontend не собран: $FRONTEND_DIR"
    echo "  Выполните сборку frontend: ./scripts/build.sh frontend"
fi

# Проверка статики в backend
echo ""
echo ">>> Проверка статики в backend..."
if [ -d "$STATIC_DIR" ] && [ "$(ls -A $STATIC_DIR 2>/dev/null)" ]; then
    STATIC_COUNT=$(find "$STATIC_DIR" -type f | wc -l)
    echo "✓ Статика скопирована в backend: $STATIC_DIR"
    echo "  Файлов: $STATIC_COUNT"
    
    if [ -f "$STATIC_DIR/index.html" ]; then
        echo "  ✓ index.html в статике найден"
    else
        echo "  ✗ index.html в статике не найден!"
    fi
else
    echo "⚠ Статика не найдена в backend: $STATIC_DIR"
    echo "  Frontend будет недоступен в JAR файле"
fi

# Проверка содержимого JAR (опционально, требует unzip)
echo ""
echo ">>> Проверка содержимого JAR..."
if command -v unzip >/dev/null 2>&1; then
    if unzip -l "$JAR_FILE" | grep -q "static/index.html"; then
        echo "✓ Frontend статика найдена в JAR"
    else
        echo "⚠ Frontend статика не найдена в JAR"
    fi
    
    if unzip -l "$JAR_FILE" | grep -q "FeontApplication.class"; then
        echo "✓ Главный класс приложения найден в JAR"
    else
        echo "✗ Главный класс приложения не найден в JAR!"
    fi
else
    echo "⚠ unzip не установлен, проверка содержимого JAR пропущена"
fi

echo ""
echo "=== Проверка завершена ==="
echo ""
if [ -f "$JAR_FILE" ]; then
    echo "JAR файл готов к развёртыванию:"
    echo "  $JAR_FILE"
    echo ""
    echo "Для развёртывания выполните:"
    echo "  ./scripts/deploy.sh user1@176.108.244.252"
fi

