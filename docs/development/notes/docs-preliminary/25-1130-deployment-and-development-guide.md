# Руководство по разработке и развёртыванию FEONT

**Дата:** 2025-11-30  
**Автор:** Александр  
**Статус:** active  
**Тип:** development-guide  
**Связанные документы:**
- [project-docs.json](../../../project/project-docs.json) - основная документация проекта
- [chat-plan-25-1130-deployment.md](../chats/chat-plan/chat-plan-25-1130-deployment.md) - план развёртывания
- [modules.json](../../../project/extensions/modules/modules.json) - структура модулей

---

## Обзор

FEONT - приложение для работы с графом знаний на основе RDF/SHACL технологий. Приложение состоит из Spring Boot backend с встроенным Apache Jena Fuseki и Vue.js 3 + Quasar frontend.

Данный документ описывает процесс разработки, сборки и развёртывания приложения FEONT.

---

## Архитектура развёртывания

### Концепция

- **Единый JAR файл** (~50-100MB) включает:
  - Spring Boot приложение
  - Встроенный Apache Jena Fuseki
  - Frontend статику (Vue.js 3 + Quasar)

- **Разделение данных и приложения:**
  - Данные TDB2 хранятся на хосте: `/home/user1/feont/data/tdb2/`
  - При обновлении приложения данные сохраняются
  - JAR файл можно заменять без потери данных

### Преимущества

- **Минимизация ресурсов:** Один процесс вместо нескольких контейнеров
- **Простота обновления:** Замена одного JAR файла
- **Сохранность данных:** Независимое хранение данных на хосте
- **Единая точка входа:** Один порт (8083) для всего приложения

---

## Быстрый старт

### Локальная разработка

1. **Установка зависимостей:**
   ```bash
   cd code/frontend
   npm install
   ```

2. **Сборка проекта:**
   ```bash
   cd ../../scripts
   ./build.sh
   ```

3. **Локальное тестирование:**
   ```bash
   ./test-local.sh
   ```

### Развёртывание на сервере

**Автоматическое развёртывание (одна команда):**
```bash
./scripts/full-deploy.sh user1@176.108.244.252
```

**Пошаговое развёртывание:**
1. Подготовка сервера: `./scripts/prepare-server.sh user1@176.108.244.252`
2. Настройка systemd: `./scripts/setup-systemd.sh user1@176.108.244.252`
3. Настройка Nginx: `./scripts/setup-nginx.sh user1@176.108.244.252`
4. Развёртывание: `./scripts/deploy.sh user1@176.108.244.252`
5. Запуск: `ssh user1@176.108.244.252 "sudo systemctl start feont"`

---

## Структура проекта

```
code/
├── backend/              # Spring Boot приложение
│   ├── src/main/java/io/github/bondalen/feont/
│   │   ├── config/      # Конфигурация (Fuseki, Web, CORS)
│   │   ├── controller/  # REST контроллеры (SPARQL endpoints)
│   │   └── service/     # Сервисы (SPARQL, инициализация)
│   └── src/main/resources/
│       ├── application.yml
│       └── static/      # Frontend статика (копируется при сборке)
├── frontend/            # Vue.js 3 + Quasar приложение
│   ├── src/
│   │   ├── pages/      # Страницы приложения
│   │   ├── components/ # Компоненты
│   │   ├── services/   # Сервисы (SPARQL)
│   │   └── layouts/    # Layouts
│   └── dist/spa/       # Результат сборки
└── scripts/            # Скрипты сборки и развёртывания
```

---

## Endpoints

### Backend (порт 8083)

- `GET /health` - Health check
- `GET /ds/ping` - SPARQL ping
- `GET /ds/sparql?query=...` - SPARQL Query (GET)
- `POST /ds/sparql` - SPARQL Query (POST)
- `POST /ds/update` - SPARQL Update
- `GET /ds/data?graph=...` - Получение данных Named Graph

### Frontend

- `/` - Главная страница
- `/sparql` - SPARQL запросы
- `/graph` - Визуализация графа
- `/ontology` - Структура онтологии

---

## Named Graphs

Приложение автоматически инициализирует следующие Named Graphs:

- `urn:ontology` - Схема/онтология
- `urn:data` - Экземпляры данных
- `urn:shacl:shapes` - SHACL shapes для валидации
- `urn:ontology:history` - История изменений схемы
- `urn:validation:results` - Результаты валидации

---

## Обновление приложения

```bash
# Сборка новой версии
./scripts/build.sh

# Обновление на сервере (с резервным копированием)
./scripts/update.sh user1@176.108.244.252
```

Данные в `/home/user1/feont/data/tdb2/` сохраняются при обновлении.

---

## Скрипты развёртывания

Подробная документация по скриптам находится в: [code/scripts/README.md](../../../../code/scripts/README.md)

### Основные скрипты:

- `build.sh` - Сборка проекта (frontend + backend)
- `test-local.sh` - Локальное тестирование
- `check-build.sh` - Проверка результатов сборки
- `prepare-server.sh` - Подготовка сервера
- `setup-systemd.sh` - Настройка systemd service
- `setup-nginx.sh` - Настройка Nginx
- `deploy.sh` - Развёртывание на сервере
- `update.sh` - Обновление приложения
- `full-deploy.sh` - Полное автоматическое развёртывание

---

## Документация

### Основная документация

- [project-docs.json](../../../project/project-docs.json) - основная документация проекта
- [modules.json](../../../project/extensions/modules/modules.json) - структура модулей приложения
- [project-development.json](../../project-development.json) - планирование задач

### Планы и резюме

- [chat-plan-25-1130-deployment.md](../chats/chat-plan/chat-plan-25-1130-deployment.md) - план развёртывания
- [project-journal.json](../../../journal/project-journal.json) - журнал разработки

### Дополнительные материалы

- [code/scripts/README.md](../../../../code/scripts/README.md) - документация по скриптам

---

## Технические детали

### Требования

- Java 21
- Maven 3.8+
- Node.js >= 18.0.0
- npm >= 9.0.0

### Переменные окружения

- `FEONT_TDB2_PATH` - путь к TDB2 хранилищу (по умолчанию: `/home/user1/feont/data/tdb2`)
- `JAVA_OPTS` - опции JVM (по умолчанию: `-Xmx1G -Xms512M`)
- `VUE_APP_SPARQL_ENDPOINT` - SPARQL endpoint для frontend

### Конфигурация

- Backend: `code/backend/src/main/resources/application.yml`
- Frontend: `code/frontend/.env.development`, `.env.production`
- Systemd: `code/scripts/feont.service`
- Nginx: `code/scripts/nginx-feont.conf`

---

## Примечания

Этот документ является предварительным руководством по разработке и развёртыванию. Для актуальной информации о структуре проекта см. [project-docs.json](../../../project/project-docs.json).

