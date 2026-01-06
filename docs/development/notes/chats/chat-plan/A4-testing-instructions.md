# Инструкции по тестированию полного workflow (A.4)

**Дата:** 2026-01-01  
**Статус:** В процессе

---

## A.4.1 Тестирование экспорта

### Шаг 1: Экспорт онтологии

Выполните команду в терминале:

```bash
cd /home/alex/feont
curl -o /tmp/ontology-test.ttl http://localhost:8083/ds/ontology
```

### Шаг 2: Проверка содержимого файла

Проверьте, что файл создан и содержит данные:

```bash
ls -lh /tmp/ontology-test.ttl
head -30 /tmp/ontology-test.ttl
wc -l /tmp/ontology-test.ttl
```

**Ожидаемый результат:** Файл должен существовать, содержать RDF данные в формате Turtle, включать классы Department и Employee.

---

## A.4.2 Тестирование редактирования

### Шаг 1: Открытие файла в Protege Desktop

1. Запустите Protege Desktop (на Windows, где она установлена)
2. Выберите `File → Open...`
3. Выберите файл `/tmp/ontology-test.ttl` (или скопируйте его на Windows, если Protege установлена там)

**Альтернативный способ (если Protege на Windows, а файл в WSL):**

Скопируйте файл на Windows:
```bash
# В WSL выполните:
cp /tmp/ontology-test.ttl /mnt/c/Users/YOUR_USERNAME/Desktop/ontology-test.ttl
```

Затем откройте файл из Desktop в Protege.

### Шаг 2: Внесение изменений

Добавьте новый класс в онтологию:

1. В Protege Desktop перейдите на вкладку `Entities → Classes`
2. Нажмите кнопку "+" (или `Edit → Create class...`)
3. Создайте новый класс с IRI: `https://feont.ontoline.ru/ontology/Project`
4. Добавьте метку (`rdfs:label`): "Проект" (на русском)
5. Добавьте комментарий (`rdfs:comment`): "Проект организации" (на русском)

**Или добавьте новое свойство:**

1. Перейдите на вкладку `Entities → Object Properties` (или `Data Properties`)
2. Создайте новое свойство с IRI: `https://feont.ontoline.ru/ontology/manages`
3. Добавьте метку: "Управляет"
4. Установите domain: `Employee`
5. Установите range: `Project` (если добавили класс Project) или `Department`

### Шаг 3: Сохранение файла

1. Выберите `File → Save` (или `File → Save As...`)
2. Сохраните файл как `/tmp/ontology-test-edited.ttl` (или на Desktop, если на Windows)
3. Убедитесь, что файл сохранён в формате Turtle (`.ttl`)

### Шаг 4: Проверка изменений в файле

Проверьте, что изменения сохранены:

```bash
# Если файл в WSL:
grep "Project\|manages" /tmp/ontology-test-edited.ttl

# Или посмотрите весь файл:
cat /tmp/ontology-test-edited.ttl
```

---

## A.4.3 Тестирование импорта

### Шаг 1: Остановка приложения

```bash
pkill -f "feont-1.0.0-SNAPSHOT.jar"
sleep 2
ps aux | grep feont | grep -v grep  # Должно быть пусто
```

### Шаг 2: Бэкап TDB2

```bash
cd /home/alex/feont
BACKUP_DIR="./code/data/tdb2-backup-$(date +%Y%m%d-%H%M%S)"
cp -r ./code/data/tdb2 "$BACKUP_DIR"
echo "Backup created: $BACKUP_DIR"
ls -ld "$BACKUP_DIR"
```

Также сохраните исходный экспортированный файл:

```bash
cp /tmp/ontology-test.ttl /tmp/ontology-test-original-$(date +%Y%m%d-%H%M%S).ttl
```

### Шаг 3: Загрузка изменённого файла

**Если файл на Windows, сначала скопируйте его в WSL:**

```bash
# Скопируйте файл из Windows в WSL (если нужно):
# cp /mnt/c/Users/YOUR_USERNAME/Desktop/ontology-test-edited.ttl /tmp/
```

Затем выполните импорт:

```bash
curl -X POST \
  -F "file=@/tmp/ontology-test-edited.ttl" \
  -F "format=turtle" \
  http://localhost:8083/ds/ontology/import
```

**Важно:** Если приложение остановлено, команда вернёт ошибку соединения. В этом случае:
- Либо запустите приложение перед импортом
- Либо импортируйте после запуска приложения

### Шаг 4: Запуск приложения

```bash
cd /home/alex/feont
java -Dorg.springframework.boot.logging.LoggingSystem=none \
  -jar code/backend/target/feont-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  --feont.tdb2.path=./code/data/tdb2 \
  --server.port=8083 > /tmp/feont-app.log 2>&1 &

sleep 12
curl -s http://localhost:8083/health
```

### Шаг 5: Импорт после запуска (если не выполнен ранее)

```bash
curl -X POST \
  -F "file=@/tmp/ontology-test-edited.ttl" \
  -F "format=turtle" \
  http://localhost:8083/ds/ontology/import | python3 -m json.tool
```

### Шаг 6: Проверка изменений

Экспортируйте онтологию и проверьте изменения:

```bash
curl -s http://localhost:8083/ds/ontology > /tmp/ontology-after-import.ttl
grep -i "Project\|manages" /tmp/ontology-after-import.ttl
```

---

## A.4.4 Проверка целостности данных

### Шаг 1: Проверка данных в графе urn:ontology

Выполните SPARQL запрос для проверки всех классов:

```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'query=PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?class WHERE { GRAPH <urn:ontology> { ?class a rdfs:Class } } ORDER BY ?class' \
  http://localhost:8083/ds/sparql | python3 -m json.tool
```

Ожидаемый результат: Должны быть видны классы Department, Employee, и Project (если добавлен).

### Шаг 2: Проверка свойств в графе urn:ontology

```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'query=PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?prop WHERE { GRAPH <urn:ontology> { ?prop a rdf:Property } } ORDER BY ?prop' \
  http://localhost:8083/ds/sparql | python3 -m json.tool
```

### Шаг 3: Проверка, что другие графы не затронуты

Проверьте граф urn:data:

```bash
curl -s "http://localhost:8083/ds/data?graph=urn:data&format=turtle" | head -20
```

Проверьте граф urn:shacl:shapes:

```bash
curl -s "http://localhost:8083/ds/data?graph=urn:shacl:shapes&format=turtle" | head -20
```

**Ожидаемый результат:** Данные в этих графах не должны измениться.

### Шаг 4: Проверка через визуализацию (если доступна)

Откройте в браузере:
```
http://localhost:8083
```

Перейдите на страницу визуализации графа и убедитесь, что новые классы/свойства отображаются.

### Шаг 5: Сравнение исходного и импортированного файла

```bash
# Посмотрите различия (если есть утилита diff):
diff /tmp/ontology-test.ttl /tmp/ontology-after-import.ttl | head -50

# Или просто сравните количество строк:
wc -l /tmp/ontology-test.ttl
wc -l /tmp/ontology-after-import.ttl
```

---

## Отчет о результатах

После выполнения всех шагов сообщите о результатах:

1. **A.4.1 Экспорт:** Файл создан? Содержит данные?
2. **A.4.2 Редактирование:** Какие изменения внесены? Файл сохранён?
3. **A.4.3 Импорт:** Импорт прошёл успешно? Какие ошибки (если были)?
4. **A.4.4 Целостность:** Все проверки прошли успешно? Данные корректны?

---

## Откат изменений (если нужно)

Если что-то пошло не так, можно восстановить из бэкапа:

```bash
# Остановить приложение
pkill -f "feont-1.0.0-SNAPSHOT.jar"

# Восстановить TDB2 из бэкапа
BACKUP_DIR="./code/data/tdb2-backup-YYYYMMDD-HHMMSS"  # Укажите реальную дату бэкапа
rm -rf ./code/data/tdb2
cp -r "$BACKUP_DIR" ./code/data/tdb2

# Запустить приложение
cd /home/alex/feont
java -Dorg.springframework.boot.logging.LoggingSystem=none \
  -jar code/backend/target/feont-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  --feont.tdb2.path=./code/data/tdb2 \
  --server.port=8083 > /tmp/feont-app.log 2>&1 &
```

