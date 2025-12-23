# Настройка GitHub Pages для RDF префиксов FEONT

**Дата:** 2025-12-23  
**Автор:** AI Assistant  
**Статус:** Альтернативный вариант (не используется)  
**Репозиторий:** `https://github.com/bondalen/feont`

---

## Примечание

**Текущее решение:** Используется домен `feont.ontoline.ru` для размещения онтологии (см. `25-1223-ontoline-setup.md`).

GitHub Pages может быть использован как:
- Резервная копия онтологии
- Публичная документация онтологии
- Альтернативный endpoint для доступа к онтологии

---

## Цель

Настроить GitHub Pages для размещения онтологии FEONT, чтобы использовать стабильные HTTP URI для RDF префиксов:
- `https://bondalen.github.io/feont/ontology/` — для онтологии (классы, свойства)

---

## Преимущества использования GitHub Pages

1. ✅ **Бесплатный хостинг** — GitHub Pages предоставляется бесплатно
2. ✅ **Стабильные URI** — адреса не изменяются
3. ✅ **Автоматическое развертывание** — при коммитах в репозиторий
4. ✅ **Публичный доступ** — онтология доступна всем
5. ✅ **Версионирование** — через Git
6. ✅ **HTTP Content Negotiation** — поддержка разных форматов (Turtle, RDF/XML, JSON-LD)

---

## Варианты настройки

### Вариант 1: Использование папки `docs/` (рекомендуется)

**Преимущества:**
- Простота настройки
- Онтология хранится вместе с кодом
- Легко обновлять

**Шаги:**

1. **Создать структуру папок:**
   ```bash
   mkdir -p docs/ontology
   ```

2. **Создать файл онтологии:**
   ```bash
   # docs/ontology/feont-ontology.ttl
   @prefix feont: <https://bondalen.github.io/feont/ontology/> .
   @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
   @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
   @prefix owl: <http://www.w3.org/2002/07/owl#> .
   
   # Классы
   feont:Department rdf:type rdfs:Class ;
                    rdfs:label "Отдел"@ru ;
                    rdfs:comment "Организационное подразделение"@ru .
   
   feont:Employee rdf:type rdfs:Class ;
                 rdfs:label "Сотрудник"@ru ;
                 rdfs:comment "Работник организации"@ru .
   
   # Свойства
   feont:name rdf:type rdf:Property ;
              rdfs:label "Название"@ru ;
              rdfs:domain rdfs:Resource .
   
   feont:worksIn rdf:type rdf:Property ;
                rdfs:label "Работает в"@ru ;
                rdfs:domain feont:Employee ;
                rdfs:range feont:Department .
   ```

3. **Создать index.html для перенаправления:**
   ```html
   <!-- docs/ontology/index.html -->
   <!DOCTYPE html>
   <html>
   <head>
       <meta http-equiv="refresh" content="0; url=feont-ontology.ttl">
       <link rel="alternate" type="text/turtle" href="feont-ontology.ttl">
   </head>
   <body>
       <p>Redirecting to <a href="feont-ontology.ttl">feont-ontology.ttl</a></p>
   </body>
   </html>
   ```

4. **Включить GitHub Pages:**
   - Перейти в Settings репозитория
   - Раздел "Pages"
   - Source: выбрать "Deploy from a branch"
   - Branch: выбрать `main` (или `master`)
   - Folder: выбрать `/docs`
   - Сохранить

5. **Проверить доступность:**
   - Онтология будет доступна по адресу: `https://bondalen.github.io/feont/ontology/feont-ontology.ttl`
   - Через несколько минут после включения

---

### Вариант 2: Использование branch `gh-pages`

**Преимущества:**
- Отдельная ветка для документации
- Не засоряет основную ветку

**Шаги:**

1. **Создать branch `gh-pages`:**
   ```bash
   git checkout -b gh-pages
   ```

2. **Создать структуру:**
   ```bash
   mkdir -p ontology
   # Создать файлы онтологии в папке ontology/
   ```

3. **Включить GitHub Pages:**
   - Settings → Pages
   - Source: выбрать branch `gh-pages`
   - Folder: `/ (root)`

4. **Обновление:**
   - Переключаться на `gh-pages` для обновления онтологии
   - Или использовать GitHub Actions для автоматического деплоя

---

## Структура URI

После настройки GitHub Pages:

```
https://bondalen.github.io/feont/ontology/          # Корень онтологии
https://bondalen.github.io/feont/ontology/feont-ontology.ttl  # Файл онтологии
https://bondalen.github.io/feont/ontology/v1.0/     # Версионирование (опционально)
```

**RDF префикс:**
```
feont: → https://bondalen.github.io/feont/ontology/
```

**Примеры использования:**
```turtle
@prefix feont: <https://bondalen.github.io/feont/ontology/> .

feont:Department rdf:type rdfs:Class .
feont:Employee rdf:type rdfs:Class .
```

---

## Миграция с текущего префикса

### Текущий префикс (development):
```
feont: → http://example.org/feont/
```

### Новый префикс (production):
```
feont: → https://bondalen.github.io/feont/ontology/
```

### План миграции:

1. **Обновить конфигурацию префиксов:**
   - `docs/project/extensions/ontology/prefixes.json`
   - `docs/project/project-docs.json`

2. **Обновить код:**
   - Константы в Java коде (если есть)
   - Frontend конфигурация
   - Примеры запросов

3. **Обновить данные:**
   - SPARQL UPDATE запросы для изменения URI в существующих данных
   - Или использовать SPARQL для массовой замены

4. **Тестирование:**
   - Проверить работу всех компонентов с новым префиксом
   - Проверить доступность онтологии через GitHub Pages

---

## HTTP Content Negotiation

GitHub Pages поддерживает HTTP Content Negotiation через заголовки:

**Запрос Turtle:**
```bash
curl -H "Accept: text/turtle" https://bondalen.github.io/feont/ontology/
```

**Запрос RDF/XML:**
```bash
curl -H "Accept: application/rdf+xml" https://bondalen.github.io/feont/ontology/
```

**Запрос JSON-LD:**
```bash
curl -H "Accept: application/ld+json" https://bondalen.github.io/feont/ontology/
```

---

## Проверка настройки

После настройки GitHub Pages проверить:

1. **Доступность онтологии:**
   ```bash
   curl https://bondalen.github.io/feont/ontology/feont-ontology.ttl
   ```

2. **Проверка в браузере:**
   - Открыть `https://bondalen.github.io/feont/ontology/feont-ontology.ttl`
   - Должен отображаться Turtle файл

3. **Проверка в RDF инструментах:**
   - Загрузить онтологию в Protégé
   - Проверить парсинг в rdflib.js

---

## Рекомендации

1. **Версионирование:**
   - Использовать папки для версий: `ontology/v1.0/`, `ontology/v1.1/`
   - Или использовать Git tags для версионирования

2. **Документация:**
   - Создать README.md в папке `docs/ontology/`
   - Описать структуру онтологии
   - Добавить примеры использования

3. **Автоматизация:**
   - Использовать GitHub Actions для автоматического обновления онтологии
   - Генерация онтологии из кода (если возможно)

---

## Связанные документы

- `docs/development/notes/docs-preliminary/25-1223-rdf-prefixes-proposal.md` — предложения по префиксам
- `docs/project/extensions/ontology/prefixes.json` — определение префиксов
- `docs/project/project-docs.json` — основная документация проекта

---

## Примечания

- GitHub Pages может занять несколько минут для активации после первой настройки
- Изменения в репозитории автоматически развертываются на GitHub Pages
- Для приватных репозиториев GitHub Pages доступен только для владельца (или платный план)

