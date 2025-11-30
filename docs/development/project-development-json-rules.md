# Правила ведения project-development.json

**Версия:** 1.0.0  
**Последнее обновление:** 2025-11-30  
**Формат версии:** 2.0.0  
**Связанные документы:**
- [`../project-documentation-rules.md`](../project-documentation-rules.md) - общие правила документации
- [`project-development.json`](project-development.json) - основной файл планирования

---

## Назначение

`project-development.json` - файл для планирования разработки проекта FEONT, содержащий:
- Метаданные планирования
- Словарь всех задач проекта (tasks)
- Иерархическую структуру задач (structure)

**Формат версии:** 2.0.0 (разделение tasks и structure)

---

## Структура файла

### Корневая структура

```json
{
  "development": {
    "metadata": {...},
    "tasks": {...},
    "structure": {...}
  }
}
```

---

## Секция metadata

### Поля

- `version` - версия формата (string, всегда "2.0.0")
- `created` - дата создания (string, формат: YYYY-MM-DD)
- `lastUpdated` - дата последнего обновления (string, формат: YYYY-MM-DDTHH:MM:SSZ)
- `description` - описание планирования (string)

### Обновление

- Обновлять `lastUpdated` при любых изменениях

---

## Секция tasks

### Структура

```json
{
  "tasks": {
    "description": "Словарь всех задач проекта",
    "nextTaskId": "0001",
    "items": {
      "task-0001": {...},
      "task-0002": {...}
    }
  }
}
```

### Поля словаря

- `description` - описание секции (string)
- `nextTaskId` - следующий свободный ID задачи (string, формат: XXXX)
- `items` - словарь всех задач (object)

---

## Структура задачи (tasks.items)

### Обязательные поля

- `id` - уникальный идентификатор задачи (string, формат: "task-XXXX")
- `title` - название задачи (string)
- `description` - описание задачи (string)
- `status` - статус задачи (string: "pending", "in_progress", "completed", "cancelled")
- `created` - дата создания (string, формат: YYYY-MM-DD)
- `lastUpdated` - дата последнего обновления (string, формат: YYYY-MM-DDTHH:MM:SSZ)

### Опциональные поля

- `assignee` - исполнитель (string)
- `priority` - приоритет (string: "low", "medium", "high", "critical")
- `completionCriteria` - критерии завершения (array of strings)
- `projectDocsRefs` - ссылки на разделы документации (array of strings)
- `journalRefs` - ссылки на записи журнала (array of strings)
- `dependencies` - зависимости от других задач (array of task IDs)
- `tags` - теги для категоризации (array of strings)

### Формат ID задачи

- Формат: `task-XXXX`
- XXXX - четырёхзначное число с ведущими нулями
- Примеры: `task-0001`, `task-0002`, `task-0123`

### Обновление nextTaskId

При создании новой задачи:
1. Использовать текущее значение `nextTaskId` для ID задачи
2. Увеличить `nextTaskId` на 1 (с ведущими нулями)
3. Обновить `lastUpdated`

---

## Секция structure

### Структура

```json
{
  "structure": {
    "description": "Иерархическая структура пунктов задач",
    "trees": [
      {
        "id": "tree-001",
        "title": "Название дерева",
        "items": [...]
      }
    ]
  }
}
```

### Поля

- `description` - описание секции (string)
- `trees` - массив деревьев задач (array)

---

## Структура дерева (structure.trees)

### Поля дерева

- `id` - уникальный идентификатор дерева (string, формат: "tree-XXX")
- `title` - название дерева (string)
- `items` - массив пунктов структуры (array)

---

## Структура пункта (structure.trees[].items)

### Обязательные поля

- `id` - уникальный идентификатор пункта (string)
- `title` - название пункта (string)
- `taskId` - ID связанной задачи из tasks.items (string, опционально)
- `status` - статус пункта (string: "pending", "in_progress", "completed", "cancelled")

### Опциональные поля

- `description` - описание пункта (string)
- `items` - подпункты (array, рекурсивная структура)
- `emoji` - эмодзи для визуализации (string, опционально)

### Иерархия

- Пункты могут иметь подпункты (массив `items`)
- Максимальная глубина не ограничена
- Каждый пункт может быть связан с задачей через `taskId`

### Формат ID пункта

- Формат: `XX.YY` или `XX.YY.ZZ` (иерархический)
- Примеры: `01.0`, `01.1`, `01.1.1`, `02.0`

---

## Правила создания задач

### Процесс создания

1. Создать задачу в `tasks.items`:
   - Использовать `nextTaskId` для ID
   - Заполнить обязательные поля
   - Обновить `nextTaskId`

2. Добавить в структуру:
   - Создать или обновить пункт в `structure.trees[].items`
   - Связать через `taskId`

3. Зафиксировать в журнале:
   - Создать запись в `project-journal.json`

### Критерии завершения

- Формулировать конкретно и измеримо
- Примеры:
  - "Создан файл X с функциональностью Y"
  - "Все тесты пройдены"
  - "Документация обновлена"

---

## Правила создания структурных пунктов

### Иерархическая нумерация

- Первый уровень: `01.0`, `02.0`, `03.0`
- Второй уровень: `01.1`, `01.2`, `02.1`
- Третий уровень: `01.1.1`, `01.1.2`

### Статусы

- `pending` - не начато
- `in_progress` - в работе
- `completed` - завершено
- `cancelled` - отменено

### Связь с задачами

- Пункт может быть связан с задачей через `taskId`
- Один пункт - одна задача (но задача может быть в нескольких пунктах)

---

## Правила обновления

### При изменении статуса задачи

1. Обновить `status` в `tasks.items[taskId]`
2. Обновить `lastUpdated` в задаче
3. Обновить `status` в связанных пунктах структуры
4. Зафиксировать в журнале

### При завершении задачи

1. Установить `status: "completed"` в задаче
2. Обновить все связанные пункты структуры
3. Зафиксировать в журнале
4. Проверить выполнение критериев завершения

### При изменении структуры

1. Обновить пункты в `structure.trees[].items`
2. Обновить `lastUpdated` в метаданных
3. Зафиксировать в журнале

---

## Зависимости между задачами

### Правила

- Задачи могут зависеть от других задач через `dependencies`
- Зависимости - массив ID задач
- Не должно быть циклических зависимостей

### Пример

```json
{
  "task-0002": {
    "id": "task-0002",
    "dependencies": ["task-0001"],
    ...
  }
}
```

---

## Связь с документацией

### projectDocsRefs

Ссылки на разделы `project-docs.json`:
- Формат: `"project:section:subsection"`
- Примеры:
  - `"project:architecture:backend"`
  - `"project:ontology"`
  - `"project:knowledge_graph:named_graphs"`

### journalRefs

Ссылки на записи `project-journal.json`:
- Формат: `"session-YYYY-MM-DD-N"` или `"log-YYYY-MM-DD-N"`

---

## Архитектура данных v2.0

### Разделение tasks и structure

**Версия 2.0** разделяет:
- **tasks** - все задачи проекта (словарь)
- **structure** - иерархическая организация задач

### Преимущества

- Задача может быть в нескольких местах структуры
- Легче отслеживать зависимости
- Гибкая организация задач

### DAG поддержка

- Задачи могут иметь зависимости (DAG - Directed Acyclic Graph)
- Структура представляет иерархию
- Одна задача - один объект, но может быть в нескольких пунктах

---

## Примеры

### Пример 1: Создание задачи

```json
{
  "tasks": {
    "nextTaskId": "0002",
    "items": {
      "task-0001": {
        "id": "task-0001",
        "title": "Настройка Apache Jena Fuseki",
        "description": "Настроить Fuseki сервер для работы с TDB2",
        "status": "in_progress",
        "created": "2025-11-30",
        "lastUpdated": "2025-11-30T20:00:00Z",
        "priority": "high",
        "completionCriteria": [
          "Fuseki доступен на http://localhost:3030",
          "SPARQL endpoint отвечает на запросы",
          "Named Graphs настроены"
        ],
        "projectDocsRefs": [
          "project:architecture:infrastructure",
          "project:knowledge_graph:named_graphs"
        ]
      }
    }
  }
}
```

### Пример 2: Структурный пункт

```json
{
  "structure": {
    "trees": [
      {
        "id": "tree-001",
        "title": "Этап 1 - Базовая инфраструктура",
        "items": [
          {
            "id": "01.1",
            "title": "Настройка Apache Jena Fuseki",
            "taskId": "task-0001",
            "status": "in_progress",
            "items": [
              {
                "id": "01.1.1",
                "title": "Создать Docker контейнер",
                "status": "completed"
              }
            ]
          }
        ]
      }
    ]
  }
}
```

---

## Валидация

### Формат JSON

- Файл должен быть валидным JSON
- Проверять синтаксис перед коммитом

### Целостность данных

- Все `taskId` в структуре должны существовать в `tasks.items`
- Все ID в `dependencies` должны существовать
- Нет циклических зависимостей

---

## Связанные документы

- [`project-development.json`](project-development.json) - основной файл
- [`../project-documentation-rules.md`](../project-documentation-rules.md) - общие правила
- [`../project/project-docs.json`](../project/project-docs.json) - документация проекта
- [`../journal/project-journal.json`](../journal/project-journal.json) - журнал

---

**Примечание:** Все задачи должны быть связаны с кодом приложения (папка `code/`) через описания и ссылки на документацию.
