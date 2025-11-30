# Backend - FEONT

Spring Boot приложение с встроенным Apache Jena Fuseki.

## Структура

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/github/bondalen/feont/
│   │   │       ├── FeontApplication.java    # Главный класс
│   │   │       ├── config/                  # Конфигурация
│   │   │       ├── controller/              # REST контроллеры
│   │   │       ├── service/                 # Бизнес-логика
│   │   │       └── model/                   # Модели данных
│   │   └── resources/
│   │       ├── application.yml              # Основная конфигурация
│   │       ├── application-dev.yml          # Конфигурация для разработки
│   │       ├── application-prod.yml         # Конфигурация для production
│   │       └── static/                      # Статические файлы (frontend)
│   └── test/                                # Тесты
├── pom.xml                                  # Maven конфигурация
└── README.md
```

## Требования

- Java 21
- Maven 3.8+

## Сборка

```bash
# Сборка JAR файла
mvn clean package

# Сборка с пропуском тестов
mvn clean package -DskipTests

# Результат: target/feont-1.0.0-SNAPSHOT.jar
```

## Запуск

```bash
# Локальный запуск
java -jar target/feont-1.0.0-SNAPSHOT.jar

# С указанием пути к TDB2
java -jar target/feont-1.0.0-SNAPSHOT.jar \
  --feont.tdb2.path=./data/tdb2

# С использованием переменной окружения
FEONT_TDB2_PATH=/path/to/tdb2 java -jar target/feont-1.0.0-SNAPSHOT.jar
```

## Конфигурация

Основные параметры настраиваются в `application.yml`:
- Порт сервера: `server.port` (по умолчанию 8083)
- Путь к TDB2: `feont.tdb2.path` (можно переопределить через `FEONT_TDB2_PATH`)
- Имя dataset: `feont.fuseki.dataset-name` (по умолчанию "ds")

## Зависимости

- Spring Boot 3.2.0
- Apache Jena 4.9.0 (Fuseki, TDB2, SHACL)
- Java 21

