# Настройка RDF префиксов на домене ontoline.ru

**Дата:** 2025-12-23  
**Автор:** AI Assistant  
**Статус:** Основная инструкция (принятое решение)  
**Домен:** `feont.ontoline.ru`

---

## Примечание

**Статус:** Это основной вариант размещения онтологии, принятый для проекта FEONT. Префикс `https://feont.ontoline.ru/ontology/` уже обновлен во всех конфигурациях проекта.

---

## Цель

Настроить размещение онтологии FEONT на домене `feont.ontoline.ru`, чтобы использовать стабильные HTTP URI для RDF префиксов:
- `https://feont.ontoline.ru/ontology/` — для онтологии (классы, свойства)

**Текущее состояние:**
- ✅ Префикс обновлен во всех конфигурациях проекта
- ✅ Создан класс `OntologyConstants` для управления префиксами
- ⏳ Требуется настройка размещения файлов онтологии на сервере (при развертывании)

---

## Преимущества использования домена ontoline.ru

1. ✅ **Единая инфраструктура** — онтология размещается на том же сервере, что и приложение
2. ✅ **Полный контроль** — управление конфигурацией и доступом
3. ✅ **Производительность** — онтология доступна с того же сервера
4. ✅ **SSL сертификат** — уже планируется для домена
5. ✅ **Гибкость** — можно настроить любую структуру URI
6. ✅ **Интеграция** — онтология может быть частью приложения
7. ✅ **Приватность** — контроль над доступом к онтологии

---

## Текущее состояние

**Настроено:**
- ✅ Домен: `feont.ontoline.ru` (указан в Nginx конфигурации)
- ✅ Nginx конфигурация: `code/scripts/nginx-feont.conf`
- ✅ Планируется SSL сертификат для HTTPS

**Требуется:**
- ⏳ Настроить размещение статических файлов онтологии
- ⏳ Настроить Nginx для обслуживания онтологии
- ⏳ Создать файлы онтологии

---

## Варианты размещения онтологии

### Вариант 1: Статические файлы через Nginx (рекомендуется)

**Преимущества:**
- Простота настройки
- Высокая производительность
- Легко обновлять

**Шаги:**

1. **Создать директорию на сервере:**
   ```bash
   sudo mkdir -p /var/www/feont/ontology
   sudo chown -R user1:user1 /var/www/feont
   ```

2. **Создать файл онтологии:**
   ```bash
   # /var/www/feont/ontology/feont-ontology.ttl
   @prefix feont: <https://feont.ontoline.ru/ontology/> .
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

3. **Обновить Nginx конфигурацию:**

   Добавить в `nginx-feont.conf` секцию для обслуживания онтологии:

   ```nginx
   # Обслуживание онтологии (статические файлы)
   location /ontology/ {
       alias /var/www/feont/ontology/;
       default_type text/turtle;
       
       # Поддержка разных форматов через расширения файлов
       types {
           text/turtle ttl;
           application/rdf+xml rdf xml;
           application/ld+json jsonld;
       }
       
       # CORS заголовки для доступа из браузера
       add_header Access-Control-Allow-Origin *;
       add_header Access-Control-Allow-Methods "GET, OPTIONS";
       add_header Access-Control-Allow-Headers "Accept";
       
       # Кэширование
       expires 1h;
       add_header Cache-Control "public, immutable";
   }
   ```

4. **Проверить и перезагрузить Nginx:**
   ```bash
   sudo nginx -t
   sudo systemctl reload nginx
   ```

5. **Проверить доступность:**
   ```bash
   curl https://feont.ontoline.ru/ontology/feont-ontology.ttl
   ```

---

### Вариант 2: Онтология как часть приложения

**Преимущества:**
- Онтология управляется через приложение
- Динамическое обновление
- Интеграция с SPARQL endpoint

**Шаги:**

1. **Создать контроллер для онтологии:**
   ```java
   @RestController
   @RequestMapping("/ontology")
   public class OntologyController {
       
       @GetMapping(value = "/", produces = "text/turtle")
       public String getOntology() {
           // Загрузить онтологию из TDB2 или файла
           // Вернуть в формате Turtle
       }
   }
   ```

2. **Разместить файлы онтологии в ресурсах:**
   - `src/main/resources/ontology/feont-ontology.ttl`

3. **Настроить маршрутизацию:**
   - Онтология будет доступна через Spring Boot приложение
   - URL: `https://feont.ontoline.ru/ontology/`

---

## Структура URI

После настройки:

```
https://feont.ontoline.ru/ontology/          # Корень онтологии
https://feont.ontoline.ru/ontology/feont-ontology.ttl  # Файл онтологии
https://feont.ontoline.ru/ontology/v1.0/     # Версионирование (опционально)
```

**RDF префикс:**
```
feont: → https://feont.ontoline.ru/ontology/
```

**Примеры использования:**
```turtle
@prefix feont: <https://feont.ontoline.ru/ontology/> .

feont:Department rdf:type rdfs:Class .
feont:Employee rdf:type rdfs:Class .
```

---

## Настройка SSL (HTTPS)

**Текущее состояние:**
- SSL сертификат планируется для домена `feont.ontoline.ru`
- В Nginx конфигурации есть закомментированная секция HTTPS

**Шаги для включения HTTPS:**

1. **Получить SSL сертификат:**
   - Использовать существующие сертификаты `ontoline.ru`
   - Или настроить Let's Encrypt для поддомена

2. **Раскомментировать HTTPS секцию в Nginx:**
   ```nginx
   server {
       listen 443 ssl http2;
       listen [::]:443 ssl http2;
       server_name feont.ontoline.ru;
       
       ssl_certificate /etc/ssl/certs/ontoline.ru.crt;
       ssl_certificate_key /etc/ssl/private/ontoline.ru.key;
       
       # ... остальная конфигурация
   }
   ```

3. **Добавить редирект с HTTP на HTTPS:**
   ```nginx
   server {
       listen 80;
       server_name feont.ontoline.ru;
       return 301 https://$server_name$request_uri;
   }
   ```

---

## HTTP Content Negotiation

Nginx можно настроить для поддержки HTTP Content Negotiation:

```nginx
location /ontology/ {
    alias /var/www/feont/ontology/;
    
    # Content Negotiation по заголовку Accept
    if ($http_accept ~ "application/rdf\+xml") {
        rewrite ^/ontology/(.*)$ /ontology/$1.rdf break;
    }
    if ($http_accept ~ "application/ld\+json") {
        rewrite ^/ontology/(.*)$ /ontology/$1.jsonld break;
    }
    
    default_type text/turtle;
}
```

**Запросы:**
```bash
# Turtle (по умолчанию)
curl https://feont.ontoline.ru/ontology/feont-ontology.ttl

# RDF/XML
curl -H "Accept: application/rdf+xml" https://feont.ontoline.ru/ontology/

# JSON-LD
curl -H "Accept: application/ld+json" https://feont.ontoline.ru/ontology/
```

---

## Миграция префикса (выполнено)

### Старый префикс:
```
feont: → http://example.org/feont/
```

### Новый префикс (уже обновлен):
```
feont: → https://feont.ontoline.ru/ontology/
```

### Выполненные действия:

1. ✅ **Обновлена конфигурация префиксов:**
   - `docs/project/extensions/ontology/prefixes.json`
   - `docs/project/project-docs.json`
   - `docs/project/extensions/ontology/ontology-structure.json`

2. ✅ **Обновлен код:**
   - Создан класс `OntologyConstants` с константами префиксов
   - Обновлен `DatasetInitializationService.java`
   - Обновлен `GraphPage.vue` (frontend)

3. ⏳ **Обновление данных (при необходимости):**
   - Если есть существующие данные со старым префиксом, можно использовать SPARQL для массовой замены:
     ```sparql
     DELETE { ?s ?p <http://example.org/feont/Department> }
     INSERT { ?s ?p <https://feont.ontoline.ru/ontology/Department> }
     WHERE { ?s ?p <http://example.org/feont/Department> }
     ```

4. ⏳ **Тестирование:**
   - Проверить работу всех компонентов с новым префиксом (при локальном тестировании)
   - Проверить доступность онтологии через домен (при развертывании)

---

## Версионирование онтологии

**Структура для версионирования:**
```
/var/www/feont/ontology/
├── feont-ontology.ttl          # Текущая версия
├── v1.0/
│   └── feont-ontology.ttl      # Версия 1.0
└── v1.1/
    └── feont-ontology.ttl      # Версия 1.1
```

**URI для версий:**
```
https://feont.ontoline.ru/ontology/          # Текущая версия
https://feont.ontoline.ru/ontology/v1.0/     # Версия 1.0
https://feont.ontoline.ru/ontology/v1.1/     # Версия 1.1
```

---

## Проверка настройки

После настройки проверить:

1. **Доступность онтологии:**
   ```bash
   curl https://feont.ontoline.ru/ontology/feont-ontology.ttl
   ```

2. **Проверка в браузере:**
   - Открыть `https://feont.ontoline.ru/ontology/feont-ontology.ttl`
   - Должен отображаться Turtle файл

3. **Проверка в RDF инструментах:**
   - Загрузить онтологию в Protégé
   - Проверить парсинг в rdflib.js

4. **Проверка префикса:**
   ```sparql
   PREFIX feont: <https://feont.ontoline.ru/ontology/>
   
   SELECT ?class WHERE {
     ?class rdf:type rdfs:Class
   }
   ```

---

## Рекомендации

1. **Резервное копирование:**
   - Регулярно делать бэкап файлов онтологии
   - Хранить версии в Git репозитории

2. **Документация:**
   - Создать README.md в папке онтологии
   - Описать структуру онтологии
   - Добавить примеры использования

3. **Мониторинг:**
   - Настроить мониторинг доступности онтологии
   - Логировать запросы к онтологии

4. **Безопасность:**
   - Ограничить доступ к онтологии (если требуется)
   - Использовать HTTPS для защиты данных

---

## Связанные документы

- `docs/development/notes/docs-preliminary/25-1223-rdf-prefixes-proposal.md` — предложения по префиксам
- `docs/project/extensions/ontology/prefixes.json` — определение префиксов
- `docs/project/project-docs.json` — основная документация проекта
- `code/scripts/nginx-feont.conf` — конфигурация Nginx

---

## Примечания

- Онтология должна быть доступна до запуска приложения
- Рекомендуется настроить автоматическое обновление онтологии при изменениях
- Для production рекомендуется использовать HTTPS (SSL сертификат)

