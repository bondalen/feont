# Правила ведения project-docs.json

**Версия:** 1.0.0  
**Последнее обновление:** 2025-11-30  
**Связанные документы:**
- [`../project-documentation-rules.md`](../project-documentation-rules.md) - общие правила документации
- [`project-docs.json`](project-docs.json) - основной файл документации

---

## Назначение

`project-docs.json` - центральный файл документации проекта FEONT, содержащий:
- Метаданные проекта
- Описание архитектуры
- Технологический стек
- Структуру графа знаний и онтологии

---

## Структура файла

### Корневая структура

```json
{
  "project": {
    "metadata": {...},
    "architecture": {...},
    "ontology": {...}
  }
}
```

---

## Секция metadata

### Обязательные поля

- `name` - название проекта (string)
- `version` - версия проекта (string, формат: X.Y.Z)
- `description` - описание проекта (string)
- `author` - автор проекта (string)
- `created` - дата создания (string, формат: YYYY-MM-DD)
- `lastUpdated` - дата последнего обновления (string, формат: YYYY-MM-DDTHH:MM:SSZ)
- `status` - статус проекта (string: "planning", "development", "production", etc.)
- `documentationRules` - путь к правилам документации (string)

### Обновление

- Обновлять `lastUpdated` при любых изменениях
- Обновлять `version` при значительных изменениях

---

## Секция architecture

### Подсекции

1. **Основные характеристики:**
   - `type` - тип приложения (string)
   - `pattern` - архитектурный паттерн (string)
   - `description` - описание архитектуры (string)

2. **backend:**
   - `framework` - фреймворк backend (string)
   - `language` - язык программирования (string)
   - `storage` - хранилище данных (string)
   - `api` - API (string)
   - `validation` - инструменты валидации (string)
   - `reasoning` - инструменты логического вывода (string, опционально)

3. **frontend:**
   - `framework` - фреймворк frontend (string)
   - `ui` - UI библиотека (string)
   - `graph_visualization` - библиотека визуализации графов (string)
   - `ontology_visualization` - библиотека визуализации онтологии (string)
   - `sparql_ui` - UI для SPARQL (string)
   - `rdf_libraries` - библиотеки для работы с RDF (string)
   - `forms` - библиотека форм (string)

4. **knowledge_graph:**
   - `format` - форматы RDF (string)
   - `ontology_language` - языки онтологии (string)
   - `validation` - язык валидации (string)
   - `query_language` - язык запросов (string)
   - `description` - описание графа знаний (string)
   - `named_graphs` - Named Graphs для организации данных (object)

5. **infrastructure:**
   - `storage` - хранилище (string)
   - `sparql_server` - SPARQL сервер (string)
   - `deployment` - конфигурация развёртывания (object)
   - `tools` - инструменты разработки (object)

---

## Секция knowledge_graph.named_graphs

### Структура

Каждый Named Graph описывается объектом с полями:
- `uri` - URI Named Graph (string)
- `description` - описание назначения (string)
- `purpose` - детальное описание использования (string)

### Стандартные Named Graphs

- `urn:ontology` - схема/онтология
- `urn:data` - экземпляры данных
- `urn:shacl:shapes` - SHACL shapes
- `urn:ontology:history` - история изменений схемы
- `urn:validation:results` - результаты валидации

### Обновление

При добавлении новых Named Graphs:
1. Добавить описание в `named_graphs`
2. Обновить документацию
3. Зафиксировать в журнале

---

## Секция ontology

### Обязательные поля

- `description` - описание онтологии (string)
- `version` - версия онтологии (string, формат: X.Y.Z)
- `lastUpdated` - дата последнего обновления (string, формат: YYYY-MM-DDTHH:MM:SSZ)
- `base_uri` - базовый URI онтологии (string)
- `dynamic_schema` - динамическая схема (boolean)
- `prefixes` - RDF префиксы (object)
- `main_concepts` - основные концепции (array)

### Структура prefixes

Каждый префикс описывается объектом:
- `uri` - URI префикса (string)
- `description` - описание назначения (string)

### Структура main_concepts

Каждая концепция описывается объектом:
- `name` - название концепции (string)
- `description` - описание концепции (string)

---

## Правила обновления

### При изменении архитектуры

1. Обновить соответствующий раздел
2. Обновить `metadata.lastUpdated`
3. При значительных изменениях обновить `metadata.version`
4. Зафиксировать изменение в журнале разработки

### При изменении онтологии

1. Обновить `ontology.version`
2. Обновить `ontology.lastUpdated`
3. Обновить описание изменений (если требуется)
4. Зафиксировать в журнале (секция `ontology_changes`)

### При добавлении технологии

1. Добавить в соответствующий раздел архитектуры
2. Обновить `lastUpdated`
3. Документировать в журнале

---

## Расширения

### Структура расширений

Расширения хранятся в `docs/project/extensions/`:
- `modules/` - структура модулей
- `ontology/` - детальное описание онтологии
- `shacl/` - каталог SHACL shapes
- `sparql/` - библиотека SPARQL запросов
- `visualization/` - конфигурации визуализации
- `deployment/` - конфигурация развёртывания

### Связь с основным файлом

Расширения дополняют `project-docs.json`, но не заменяют его. Основная информация должна быть в `project-docs.json`, детали - в расширениях.

---

## Примеры обновления

### Пример 1: Добавление новой технологии

```json
{
  "project": {
    "architecture": {
      "frontend": {
        "new_library": "название библиотеки",
        ...
      }
    }
  }
}
```

Затем:
1. Обновить `metadata.lastUpdated`
2. Зафиксировать в журнале

### Пример 2: Обновление версии онтологии

```json
{
  "project": {
    "ontology": {
      "version": "0.2.0",
      "lastUpdated": "2025-12-01T10:00:00Z",
      ...
    }
  }
}
```

Затем:
1. Зафиксировать изменения в журнале (`ontology_changes`)
2. Обновить Named Graph `urn:ontology:history`

---

## Валидация

### Формат JSON

- Файл должен быть валидным JSON
- Проверять синтаксис перед коммитом
- Использовать форматирование (2 пробела для отступов)

### Обязательные поля

Все обязательные поля должны присутствовать:
- `metadata`: name, version, description, author, created, lastUpdated, status
- `architecture`: type, pattern, description
- `ontology`: description, version, lastUpdated, base_uri, prefixes, main_concepts

### Согласованность данных

- Версии в формате X.Y.Z
- Даты в формате ISO 8601
- URI должны быть валидными

---

## Связь с другими документами

### С project-development.json

Задачи могут ссылаться на разделы документации:
```json
{
  "task-0001": {
    "projectDocsRefs": [
      "project:architecture:backend",
      "project:ontology"
    ]
  }
}
```

### С project-journal.json

Изменения фиксируются в журнале:
```json
{
  "sessions": [
    {
      "changes": [
        {
          "type": "architecture_updated",
          "section": "frontend",
          "description": "Добавлена новая библиотека"
        }
      ]
    }
  ]
}
```

---

## Адаптация для графа знаний

### Особенности

1. **Динамическая схема:**
   - `ontology.dynamic_schema: true`
   - Версионирование через Named Graphs
   - История в `urn:ontology:history`

2. **Named Graphs:**
   - Все Named Graphs описаны в `knowledge_graph.named_graphs`
   - При добавлении нового - обновить документацию

3. **Валидация:**
   - SHACL shapes в `urn:shacl:shapes`
   - Документирование в расширениях `shacl/`

4. **Запросы:**
   - SPARQL запросы в расширениях `sparql/`
   - Связь с задачами через документацию

---

## Связанные документы

- [`project-docs.json`](project-docs.json) - основной файл
- [`../project-documentation-rules.md`](../project-documentation-rules.md) - общие правила
- [`../development/project-development.json`](../development/project-development.json) - планирование
- [`../journal/project-journal.json`](../journal/project-journal.json) - журнал

---

**Примечание:** Все изменения в `project-docs.json` должны быть согласованы с изменениями в коде приложения (папка `code/`) и задокументированы в журнале разработки.
