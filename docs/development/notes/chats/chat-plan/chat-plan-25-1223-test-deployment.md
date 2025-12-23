# План тестового развертывания и проверки FEONT

**Дата:** 2025-12-23  
**Автор:** Александр  
**Связанные задачи:** (будут созданы в project-development.json)  
**Порядок выполнения:** C → A → B

## Контекст

Проект FEONT находится на стадии готовности к тестированию. Все основные компоненты реализованы:
- ✅ Backend с встроенным Fuseki
- ✅ Frontend на Vue.js 3 + Quasar
- ✅ SPARQL endpoints работают
- ✅ Визуализация графов реализована

**Цель:** Провести полное тестовое развертывание приложения и проверку функциональности через веб-интерфейс с вводом данных пользователем.

---

## Структурный план

### Этап C: Добавление недостающей функциональности

#### C.1 Добавление поддержки SPARQL UPDATE в интерфейс

**Статус:** ✅ Выполнено

**Выполненные действия:**
- ✅ Добавлен импорт `executeUpdate` из `sparql.service.js`
- ✅ Добавлен тип запроса 'UPDATE' в список `queryTypes`
- ✅ Добавлена обработка UPDATE запросов в `executeQuery`
- ✅ Обновлен UI для отображения типа запроса (Query / Update)
- ✅ Добавлена визуальная индикация успешного выполнения UPDATE (зеленый фон)

**Результат:**
- Компонент `SparqlPage.vue` теперь поддерживает выполнение SPARQL UPDATE запросов
- Пользователи могут вводить данные через веб-интерфейс

#### C.2 Анализ и уточнение предложений по RDF префиксам

**Статус:** ✅ Выполнено

**Выполненные действия:**
- ✅ Проанализирован вопрос о целесообразности использования Java пакета как RDF префикса
- ✅ Рассмотрены варианты: GitHub Pages, домен ontoline.ru, Java пакет
- ✅ Принято решение использовать домен `feont.ontoline.ru` для разработки и production
- ✅ Обновлены все конфигурации проекта для использования нового префикса
- ✅ Создан класс `OntologyConstants` для централизованного управления префиксами

**Выводы:**
- ❌ **Не рекомендуется** использовать Java пакет (`java:io/github/bondalen/feont/`) как RDF префикс
- ✅ **Принято решение:** использовать `https://feont.ontoline.ru/ontology/` для разработки и production
- ✅ **Преимущества:** единый префикс, не нужно переделывать при переходе в production

**Обновленные файлы:**
- `docs/project/project-docs.json` — обновлен base_uri и префикс
- `docs/project/extensions/ontology/prefixes.json` — обновлен URI префикса
- `code/backend/src/main/java/io/github/bondalen/feont/config/OntologyConstants.java` — создан новый класс
- `code/backend/src/main/java/io/github/bondalen/feont/service/DatasetInitializationService.java` — обновлен
- `code/frontend/src/pages/GraphPage.vue` — обновлен префикс (с поддержкой HTTP для локальной разработки)

**Причины выбора ontoline.ru:**
1. Домен уже настроен в Nginx конфигурации
2. Приложение будет размещено на этом домене
3. Единая инфраструктура — онтология на том же сервере
4. Полный контроль над конфигурацией

---

### Этап A: Локальное тестирование

#### A.1 Подготовка окружения

- A.1.1 Проверить наличие собранного JAR файла
  - Путь: `code/backend/target/feont-1.0.0-SNAPSHOT.jar`
  - Проверить размер и дату сборки
  
- A.1.2 Проверить наличие frontend сборки
  - Путь: `code/frontend/dist/spa/`
  - Проверить наличие файлов
  
- A.1.3 Проверить доступность порта 8083
  - Команда: `lsof -i :8083` или `netstat -tuln | grep 8083`
  
- A.1.4 Создать/проверить директорию для данных TDB2
  - Путь: `code/data/tdb2/`
  - Команда: `mkdir -p code/data/tdb2`

#### A.2 Запуск приложения локально

- A.2.1 Запустить приложение
  - Вариант 1: Использовать скрипт `./scripts/test-local.sh`
  - Вариант 2: Вручную:
    ```bash
    java -Dorg.springframework.boot.logging.LoggingSystem=none \
         -jar backend/target/feont-1.0.0-SNAPSHOT.jar \
         --spring.profiles.active=dev \
         --feont.tdb2.path=./data/tdb2 \
         --server.port=8083
    ```
  
- A.2.2 Проверить запуск
  - Health check: `curl http://localhost:8083/health`
  - Ожидаемый результат: `{"application":"FEONT","status":"UP"}`

#### A.3 Проверка базовой функциональности через браузер

- A.3.1 Открыть приложение в браузере
  - URL: `http://localhost:8083/`
  - Проверить загрузку frontend
  - Проверить консоль браузера (F12) на ошибки
  
- A.3.2 Проверить навигацию
  - Главная страница (IndexPage)
  - Страница SPARQL (SparqlPage)
  - Страница графа (GraphPage)
  - Страница онтологии (OntologyPage)

- A.3.3 Проверить SPARQL endpoints
  - Health check через браузер: `http://localhost:8083/health`
  - SPARQL query endpoint: `http://localhost:8083/ds/sparql`

#### A.4 Ввод тестовых данных через веб-интерфейс

- A.4.1 Подготовить тестовые SPARQL UPDATE запросы

**Запрос 1: Добавление классов в онтологию**

**Примечание:** Используется префикс `https://feont.ontoline.ru/ontology/`, который уже настроен в проекте.

```sparql
PREFIX feont: <https://feont.ontoline.ru/ontology/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

INSERT DATA {
  GRAPH <urn:ontology> {
    feont:Department rdf:type rdfs:Class ;
                     rdfs:label "Отдел"@ru ;
                     rdfs:comment "Организационное подразделение"@ru .
    
    feont:Employee rdf:type rdfs:Class ;
                   rdfs:label "Сотрудник"@ru ;
                   rdfs:comment "Работник организации"@ru .
    
    feont:name rdf:type rdf:Property ;
               rdfs:label "Название"@ru ;
               rdfs:domain rdfs:Resource .
    
    feont:worksIn rdf:type rdf:Property ;
                  rdfs:label "Работает в"@ru ;
                  rdfs:domain feont:Employee ;
                  rdfs:range feont:Department .
  }
}
```

**Запрос 2: Добавление экземпляров данных**
```sparql
PREFIX feont: <https://feont.ontoline.ru/ontology/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

INSERT DATA {
  GRAPH <urn:data> {
    feont:dept1 rdf:type feont:Department ;
               feont:name "Отдел разработки" ;
               feont:code "DEV-001" .
    
    feont:dept2 rdf:type feont:Department ;
               feont:name "Отдел тестирования" ;
               feont:code "QA-001" .
    
    feont:emp1 rdf:type feont:Employee ;
               feont:name "Иван Иванов" ;
               feont:worksIn feont:dept1 .
    
    feont:emp2 rdf:type feont:Employee ;
               feont:name "Петр Петров" ;
               feont:worksIn feont:dept2 .
  }
}
```

**Запрос 3: Добавление технической структуры**
```sparql
PREFIX feont: <https://feont.ontoline.ru/ontology/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

INSERT DATA {
  GRAPH <urn:ontology> {
    feont:System rdf:type rdfs:Class ;
                rdfs:label "Система"@ru .
    
    feont:Component rdf:type rdfs:Class ;
                   rdfs:label "Компонент"@ru .
    
    feont:partOf rdf:type rdf:Property ;
                 rdfs:label "Часть системы"@ru ;
                 rdfs:domain feont:Component ;
                 rdfs:range feont:System .
  }
  
  GRAPH <urn:data> {
    feont:system1 rdf:type feont:System ;
                  feont:name "Система управления" ;
                  feont:version "1.0.0" .
    
    feont:component1 rdf:type feont:Component ;
                    feont:name "API Gateway" ;
                    feont:partOf feont:system1 .
    
    feont:component2 rdf:type feont:Component ;
                    feont:name "Database" ;
                    feont:partOf feont:system1 .
  }
}
```

- A.4.2 Выполнить запросы через веб-интерфейс
  - Открыть страницу SPARQL: `http://localhost:8083/sparql`
  - Выбрать тип запроса: **UPDATE**
  - Вставить первый запрос (добавление классов)
  - Нажать "Выполнить"
  - Проверить результат (должно быть сообщение об успехе)
  - Повторить для остальных запросов

- A.4.3 Проверить запись данных
  - Выполнить SELECT запрос для проверки:
    ```sparql
    PREFIX feont: <https://feont.ontoline.ru/ontology/>
    
    SELECT ?s ?p ?o
    WHERE {
      GRAPH <urn:data> {
        ?s ?p ?o
      }
    }
    LIMIT 20
    ```
  - Проверить наличие добавленных данных

#### A.5 Проверка визуализации данных

- A.5.1 Загрузить данные в GraphPage
  - Открыть страницу графа: `http://localhost:8083/graph`
  - Нажать "Загрузить данные"
  - Проверить отображение графа в Cytoscape.js

- A.5.2 Проверить визуализацию различных типов данных
  - Визуализация организационной структуры (отделы, сотрудники)
  - Визуализация технической структуры (системы, компоненты)
  - Проверить связи между узлами

- A.5.3 Проверить интерактивность
  - Масштабирование графа (колесико мыши)
  - Перетаскивание узлов
  - Отображение меток узлов и рёбер

#### A.6 Проверка работы с Named Graphs

- A.6.1 Проверить чтение из разных графов
  - Выполнить SELECT запрос для `urn:ontology`
  - Выполнить SELECT запрос для `urn:data`
  - Выполнить SELECT запрос для `urn:shacl:shapes`

- A.6.2 Проверить запись в разные графы
  - Добавить данные в `urn:ontology`
  - Добавить данные в `urn:data`
  - Проверить разделение данных по графам

#### A.7 Документирование результатов локального тестирования

- A.7.1 Зафиксировать результаты
  - Какие функции работают корректно
  - Какие проблемы обнаружены
  - Скриншоты интерфейса (опционально)

- A.7.2 Создать список обнаруженных проблем (если есть)
  - Описание проблемы
  - Шаги для воспроизведения
  - Предложения по решению

---

### Этап B: Развертывание на сервере

#### B.1 Подготовка к развертыванию

- B.1.1 Проверить готовность сборки
  - JAR файл собран и протестирован локально
  - Frontend статика включена в JAR
  - Все тесты пройдены

- B.1.2 Проверить доступность сервера
  - Сервер: `176.108.244.252`
  - Пользователь: `user1`
  - SSH доступ: `ssh user1@176.108.244.252`

- B.1.3 Проверить наличие скриптов развертывания
  - `scripts/full-deploy.sh` — полное развертывание
  - `scripts/deploy.sh` — развертывание JAR
  - `scripts/update.sh` — обновление приложения

#### B.2 Развертывание на сервере

- B.2.1 Выполнить полное развертывание
  - Команда: `./scripts/full-deploy.sh user1@176.108.244.252`
  - Или пошагово:
    ```bash
    ./scripts/prepare-server.sh user1@176.108.244.252
    ./scripts/setup-systemd.sh user1@176.108.244.252
    ./scripts/setup-nginx.sh user1@176.108.244.252
    ./scripts/deploy.sh user1@176.108.244.252
    ```

- B.2.2 Проверить запуск приложения
  - Проверить статус systemd service: `ssh user1@176.108.244.252 "sudo systemctl status feont"`
  - Проверить логи: `ssh user1@176.108.244.252 "sudo journalctl -u feont -n 50"`

#### B.3 Проверка работы на сервере

- B.3.1 Проверить доступность приложения
  - URL: `http://176.108.244.252:8083/` или через домен (если настроен Nginx)
  - Health check: `http://176.108.244.252:8083/health`

- B.3.2 Повторить тесты из этапа A
  - Проверка навигации
  - Ввод тестовых данных через веб-интерфейс
  - Проверка визуализации
  - Проверка работы с Named Graphs

#### B.4 Финальное тестирование

- B.4.1 Провести полное функциональное тестирование
  - Все функции из этапа A
  - Проверка производительности
  - Проверка стабильности работы

- B.4.2 Проверить работу с реальными данными
  - Ввод более сложных структур данных
  - Проверка обработки больших объемов данных
  - Проверка визуализации больших графов

---

## Контрольные точки

- **K1** — Поддержка UPDATE добавлена в интерфейс (закрывает этап C) — ✅ выполнено
  - Критерии: UPDATE запросы выполняются через веб-интерфейс
  - Результат: ✅ Функциональность добавлена

- **K2** — Локальное тестирование завершено (закрывает этап A) — ⏳ планируется
  - Критерии: приложение работает локально, данные вводятся через интерфейс, визуализация работает
  - Результат: ⏳ Ожидается

- **K3** — Развертывание на сервере завершено (закрывает этап B) — ⏳ планируется
  - Критерии: приложение развернуто на сервере, все функции работают
  - Результат: ⏳ Ожидается

---

## Статус плана

- ✅ **Этап C** — Выполнен
  - ✅ Добавление поддержки UPDATE в интерфейс
  - ✅ Анализ и уточнение предложений по RDF префиксам
  - ✅ Обновление всех конфигураций для использования префикса `https://feont.ontoline.ru/ontology/`

- ⏳ **Этап A** — Планируется
  - ⏳ Локальное тестирование приложения

- ⏳ **Этап B** — Планируется
  - ⏳ Развертывание на сервере

---

## Предложения по улучшению

### Для интерфейса ввода данных

1. **Создать форму для ввода данных (опционально)**
   - Форма для добавления организационных единиц
   - Форма для добавления технических компонентов
   - Генерация SPARQL UPDATE запросов из формы

2. **Добавить шаблоны запросов**
   - Предустановленные шаблоны для часто используемых операций
   - Примеры запросов для разных сценариев

3. **Улучшить отображение результатов UPDATE**
   - Показывать количество добавленных/измененных триплетов
   - Отображать предупреждения при потенциально опасных операциях

### Для визуализации

1. **Добавить фильтрацию узлов**
   - Фильтр по типу (rdf:type)
   - Фильтр по префиксу URI
   - Поиск узлов по метке

2. **Улучшить интерактивность**
   - Клик по узлу показывает его свойства
   - Выделение связанных узлов
   - Экспорт графа в изображение

---

## Связанные документы

- `docs/project/project-docs.json` — основная документация проекта
- `docs/development/project-development.json` — планирование задач
- `docs/journal/project-journal.json` — журнал разработки
- `docs/development/notes/docs-preliminary/25-1223-rdf-prefixes-proposal.md` — анализ и решение по префиксам (принято решение)
- `docs/development/notes/docs-preliminary/25-1223-ontoline-setup.md` — инструкция по настройке онтологии на ontoline.ru (основной вариант)
- `docs/development/notes/docs-preliminary/25-1223-github-pages-setup.md` — инструкция по GitHub Pages (альтернативный вариант)
- `code/backend/src/main/java/io/github/bondalen/feont/config/OntologyConstants.java` — константы префиксов
- `code/frontend/src/pages/SparqlPage.vue` — компонент SPARQL интерфейса

---

## Примечания

**Важные замечания:**
- Перед развертыванием на сервере обязательно протестировать локально
- При проблемах с развертыванием проверить логи systemd service
- Для production рекомендуется настроить Nginx reverse proxy
- Данные TDB2 хранятся отдельно на сервере и сохраняются при обновлении приложения
- **Префикс онтологии:** `https://feont.ontoline.ru/ontology/` уже обновлен во всех конфигурациях проекта
- Для локальной разработки можно использовать HTTP версию (`http://feont.ontoline.ru/ontology/`) или настроить локальный домен через `/etc/hosts`

**Известные ограничения:**
- Визуализация больших графов (>1000 узлов) может требовать оптимизации
- UPDATE запросы не имеют отката (rollback) — нужно быть осторожным
- Нет валидации SHACL через интерфейс (только через backend)
- Онтология на домене `feont.ontoline.ru` будет доступна только после настройки на сервере (при развертывании)

