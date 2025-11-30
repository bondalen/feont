# Frontend - FEONT

Vue.js 3 + Quasar Framework приложение для работы с графом знаний.

## Структура

```
frontend/
├── src/
│   ├── components/          # Vue компоненты
│   ├── pages/              # Страницы приложения
│   ├── layouts/            # Layouts Quasar
│   ├── boot/               # Boot файлы Quasar
│   ├── router/             # Vue Router
│   ├── stores/             # Pinia stores
│   ├── css/                # Стили
│   ├── App.vue             # Главный компонент
│   └── main.js             # Точка входа
├── package.json            # Зависимости
├── quasar.config.js        # Конфигурация Quasar
└── README.md
```

## Требования

- Node.js >= 18.0.0
- npm >= 9.0.0 или yarn >= 1.22.0

## Установка зависимостей

```bash
npm install
# или
yarn install
```

## Разработка

```bash
# Запуск dev сервера
npm run dev
# или
quasar dev
```

## Сборка

```bash
# Сборка для production
npm run build
# или
quasar build -m spa
```

Результат сборки будет в папке `dist/spa/` и должен быть скопирован в `backend/src/main/resources/static/` перед сборкой JAR.

## Основные библиотеки

- **Vue.js 3** - фреймворк
- **Quasar Framework** - UI компоненты
- **Cytoscape.js** - визуализация графов
- **rdflib.js** - работа с RDF
- **n3** - парсер Turtle/N3
- **jsonld** - работа с JSON-LD
- **@formkit/vue** - динамические формы

## Конфигурация

SPARQL endpoint настраивается через переменные окружения или конфигурацию Quasar.

