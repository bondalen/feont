#!/bin/bash

# Скрипт сборки проекта FEONT
# 
# Использование:
#   ./scripts/build.sh          # Сборка backend и frontend
#   ./scripts/build.sh backend  # Только backend
#   ./scripts/build.sh frontend # Только frontend

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "=== Сборка проекта FEONT ==="
echo "Корень проекта: $PROJECT_ROOT"
echo ""

# Функция сборки backend
build_backend() {
    echo ">>> Сборка Backend (Spring Boot)..."
    cd "$PROJECT_ROOT/backend"
    
    if [ ! -f "pom.xml" ]; then
        echo "Ошибка: pom.xml не найден!"
        exit 1
    fi
    
    # Копирование frontend статики, если она собрана
    if [ -d "../frontend/dist/spa" ]; then
        echo "Копирование frontend статики..."
        rm -rf src/main/resources/static/*
        cp -r ../frontend/dist/spa/* src/main/resources/static/
    else
        echo "Предупреждение: frontend/dist/spa не найден. Frontend статика не будет включена."
        echo "Запустите сначала: npm run build в папке frontend"
    fi
    
    # Сборка Maven
    mvn clean package -DskipTests
    echo ">>> Backend собран успешно!"
    echo "JAR файл: backend/target/feont-1.0.0-SNAPSHOT.jar"
}

# Функция сборки frontend
build_frontend() {
    echo ">>> Сборка Frontend (Vue.js 3 + Quasar)..."
    cd "$PROJECT_ROOT/frontend"
    
    if [ ! -f "package.json" ]; then
        echo "Ошибка: package.json не найден!"
        exit 1
    fi
    
    # Проверка node_modules
    if [ ! -d "node_modules" ]; then
        echo "Установка зависимостей npm..."
        npm install
    fi
    
    # Сборка Quasar
    npm run build
    
    echo ">>> Frontend собран успешно!"
    echo "Результат: frontend/dist/spa/"
}

# Обработка аргументов
case "${1:-all}" in
    backend)
        build_backend
        ;;
    frontend)
        build_frontend
        ;;
    all)
        build_frontend
        build_backend
        ;;
    *)
        echo "Использование: $0 [backend|frontend|all]"
        exit 1
        ;;
esac

echo ""
echo "=== Сборка завершена! ==="

