# Правила ведения project-journal.json

**Версия:** 1.0.0  
**Последнее обновление:** 2025-11-30  
**Связанные документы:**
- [`../project-documentation-rules.md`](../project-documentation-rules.md) - общие правила документации
- [`project-journal.json`](project-journal.json) - основной файл журнала

---

## Назначение

`project-journal.json` - журнал разработки проекта FEONT, содержащий:
- Метаданные журнала
- Записи сессий работы (sessions)
- Историю изменений

---

## Структура файла

### Корневая структура

```json
{
  "journal": {
    "metadata": {...},
    "sessions": [...]
  }
}
```

---

## Секция metadata

### Поля

- `version` - версия формата (string, всегда "1.0.0")
- `status` - статус журнала (string: "active", "archived")
- `lastUpdated` - дата последнего обновления (string, формат: YYYY-MM-DDTHH:MM:SSZ)

### Обновление

- Обновлять `lastUpdated` при любых изменениях

---

## Секция sessions

### Структура

Массив записей сессий работы:
```json
{
  "sessions": [
    {
      "id": "session-2025-11-30-1",
      "date": "2025-11-30",
      "type": "chat",
      "title": "Название сессии",
      ...
    }
  ]
}
```

---

## Структура сессии

### Обязательные поля

- `id` - уникальный идентификатор сессии (string, формат: "session-YYYY-MM-DD-N")
- `date` - дата сессии (string, формат: YYYY-MM-DD)
- `type` - тип сессии (string: "chat", "work", "meeting", etc.)
- `title` - название сессии (string)
- `created` - дата создания записи (string, формат: YYYY-MM-DDTHH:MM:SSZ)

### Опциональные поля

- `author` - автор сессии (string)
- `description` - описание сессии (string)
- `developmentTasks` - связанные задачи (array of task IDs)
- `projectDocsRefs` - ссылки на документацию (array of strings)
- `journalRefs` - ссылки на другие записи журнала (array of session IDs)
- `changes` - список изменений (array of objects)
- `artifacts` - созданные/измененные артефакты (array of objects)
- `observations` - наблюдения и выводы (array of strings)
- `notes` - примечания (string)

---

## Формат ID сессии

- Формат: `session-YYYY-MM-DD-N`
- YYYY-MM-DD - дата сессии
- N - порядковый номер сессии в этот день (1, 2, 3, ...)
- Примеры: `session-2025-11-30-1`, `session-2025-12-01-2`

---

## Структура changes

### Поля изменения

- `type` - тип изменения (string: "file_created", "file_modified", "file_deleted", "architecture_updated", "ontology_updated", etc.)
- `target` - цель изменения (string: путь к файлу, раздел документации, etc.)
- `description` - описание изменения (string)
- `timestamp` - время изменения (string, формат: YYYY-MM-DDTHH:MM:SSZ)

### Пример

```json
{
  "changes": [
    {
      "type": "file_created",
      "target": "code/frontend/src/components/OntologyEditor.vue",
      "description": "Создан компонент редактора онтологии",
      "timestamp": "2025-11-30T10:30:00Z"
    },
    {
      "type": "ontology_updated",
      "target": "project:ontology",
      "description": "Добавлен новый класс feont:SignalSystem",
      "timestamp": "2025-11-30T11:00:00Z"
    }
  ]
}
```

---

## Структура artifacts

### Поля артефакта

- `type` - тип артефакта (string: "code", "documentation", "configuration", "data", etc.)
- `path` - путь к артефакту (string)
- `action` - действие (string: "created", "modified", "deleted")
- `description` - описание (string, опционально)

### Пример

```json
{
  "artifacts": [
    {
      "type": "code",
      "path": "code/frontend/src/components/OntologyEditor.vue",
      "action": "created",
      "description": "Компонент редактора онтологии"
    },
    {
      "type": "documentation",
      "path": "docs/project/project-docs.json",
      "action": "modified",
      "description": "Обновлена версия онтологии"
    }
  ]
}
```

---

## Специальные типы изменений для графа знаний

### ontology_changes

Изменения онтологии фиксируются в секции `ontology_changes`:

```json
{
  "changes": [
    {
      "type": "ontology_changes",
      "changes": [
        {
          "action": "added_class",
          "class": "feont:SignalSystem",
          "description": "Добавлен класс системы сигнализации",
          "timestamp": "2025-11-30T10:30:00Z"
        },
        {
          "action": "added_property",
          "property": "feont:hasFrequency",
          "domain": "feont:SignalSystem",
          "range": "xsd:decimal",
          "timestamp": "2025-11-30T11:00:00Z"
        }
      ]
    }
  ]
}
```

### Действия с онтологией

- `added_class` - добавлен класс
- `modified_class` - изменен класс
- `deleted_class` - удален класс
- `added_property` - добавлено свойство
- `modified_property` - изменено свойство
- `deleted_property` - удалено свойство
- `added_shape` - добавлен SHACL shape
- `modified_shape` - изменен SHACL shape

---

## Правила создания сессий

### Процесс

1. Создать новую запись сессии:
   - Генерировать уникальный ID
   - Заполнить обязательные поля
   - Указать дату и время

2. Зафиксировать изменения:
   - Добавить все изменения в `changes`
   - Добавить артефакты в `artifacts`

3. Связать с задачами:
   - Указать связанные задачи в `developmentTasks`
   - Ссылки на документацию в `projectDocsRefs`

4. Добавить наблюдения:
   - Ключевые выводы в `observations`
   - Примечания в `notes`

---

## Правила обновления

### При создании новой сессии

1. Добавить запись в массив `sessions`
2. Обновить `metadata.lastUpdated`
3. Связать с задачами и документацией

### При обновлении существующей сессии

1. Добавить новые изменения в `changes`
2. Обновить `artifacts`
3. Обновить `metadata.lastUpdated`

---

## Связь с другими документами

### developmentTasks

Ссылки на задачи из `project-development.json`:
- Формат: `"task-XXXX"`
- Пример: `["task-0001", "task-0002"]`

### projectDocsRefs

Ссылки на разделы `project-docs.json`:
- Формат: `"project:section:subsection"`
- Примеры: `["project:architecture:backend", "project:ontology"]`

### journalRefs

Ссылки на другие записи журнала:
- Формат: `"session-YYYY-MM-DD-N"`
- Пример: `["session-2025-11-29-1"]`

---

## Связь с планами и резюме чатов

### Связь с chat-plan

План работы может ссылаться на сессию журнала:
- В плане указывать связанные задачи
- Сессия журнала ссылается на те же задачи

### Связь с chat-resume

Резюме чата соответствует записи в журнале:
- Резюме содержит детальное описание
- Журнал содержит структурированные данные
- Оба связаны через задачи

---

## Документирование экспериментов

### Для исследовательского проекта

Важно фиксировать:
- Эксперименты с онтологией
- Результаты экспериментов с визуализацией
- Наблюдения о работе графа знаний
- Решения об изменениях схемы

### Формат

Добавлять в `observations`:
```json
{
  "observations": [
    "Эксперимент с визуализацией больших графов показал, что Cytoscape.js лучше подходит для интерактивных графов",
    "Добавление нового класса в онтологию заняло 5 минут без миграций",
    "SHACL валидация выявила 3 несоответствия в данных"
  ]
}
```

---

## Версионирование онтологии

### Фиксация изменений

При изменении онтологии:
1. Зафиксировать в `changes` с типом `ontology_changes`
2. Указать все изменения (классы, свойства, shapes)
3. Обновить версию в `project-docs.json`
4. Задокументировать в Named Graph `urn:ontology:history`

---

## Примеры

### Пример 1: Сессия работы

```json
{
  "id": "session-2025-11-30-1",
  "date": "2025-11-30",
  "type": "chat",
  "title": "Создание системы документации",
  "author": "Александр",
  "created": "2025-11-30T19:00:00Z",
  "description": "Создана базовая структура документации проекта FEONT",
  "developmentTasks": ["task-0001"],
  "projectDocsRefs": ["project:metadata"],
  "changes": [
    {
      "type": "file_created",
      "target": "docs/project/project-docs.json",
      "description": "Создан основной файл документации",
      "timestamp": "2025-11-30T19:10:00Z"
    }
  ],
  "artifacts": [
    {
      "type": "documentation",
      "path": "docs/project/project-docs.json",
      "action": "created"
    }
  ],
  "observations": [
    "Система документации успешно создана на основе проверенной системы из FEMSQ"
  ]
}
```

### Пример 2: Изменение онтологии

```json
{
  "id": "session-2025-12-01-1",
  "date": "2025-12-01",
  "type": "work",
  "title": "Расширение онтологии",
  "changes": [
    {
      "type": "ontology_changes",
      "changes": [
        {
          "action": "added_class",
          "class": "feont:SignalSystem",
          "description": "Добавлен класс системы сигнализации",
          "timestamp": "2025-12-01T10:30:00Z"
        }
      ]
    }
  ],
  "developmentTasks": ["task-0005"]
}
```

---

## Валидация

### Формат JSON

- Файл должен быть валидным JSON
- Проверять синтаксис перед коммитом

### Целостность данных

- Все ID в `developmentTasks` должны существовать
- Все ссылки в `projectDocsRefs` должны быть валидными
- Даты в формате ISO 8601

---

## Связанные документы

- [`project-journal.json`](project-journal.json) - основной файл
- [`../project-documentation-rules.md`](../project-documentation-rules.md) - общие правила
- [`../development/project-development.json`](../development/project-development.json) - планирование
- [`../project/project-docs.json`](../project/project-docs.json) - документация

---

**Примечание:** Все изменения в коде приложения (папка `code/`) должны фиксироваться в журнале через записи об артефактах и изменениях.
