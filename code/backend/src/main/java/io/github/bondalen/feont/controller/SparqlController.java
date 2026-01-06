package io.github.bondalen.feont.controller;

import io.github.bondalen.feont.service.SparqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST контроллер для SPARQL endpoints
 * 
 * Реализует стандартные Fuseki endpoints:
 * - /sparql - SPARQL Query (SELECT, CONSTRUCT, ASK, DESCRIBE)
 * - /update - SPARQL Update
 * - /data - доступ к данным Named Graphs
 * 
 * @author Александр
 * @version 1.0.0
 */
@RestController
@RequestMapping("/ds")
public class SparqlController {

    private static final Logger logger = LoggerFactory.getLogger(SparqlController.class);

    private final SparqlService sparqlService;

    @Autowired
    public SparqlController(SparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    /**
     * SPARQL Query endpoint
     * Поддерживает GET и POST запросы
     * 
     * GET: ?query=SPARQL_QUERY
     * POST: query в теле запроса или в form-data
     */
    @GetMapping(value = "/sparql", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sparqlQueryGet(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "format", defaultValue = "json") String format) {
        try {
            // Если запрос не указан, возвращаем информацию о endpoint
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"SPARQL Query endpoint\", \"usage\": \"Use POST method with query parameter or GET with ?query=SPARQL_QUERY\"}");
            }
            
            logger.debug("SPARQL Query (GET): {}", query.substring(0, Math.min(100, query.length())));
            
            // Определение типа запроса (ищем первое ключевое слово, игнорируя PREFIX, BASE и т.д.)
            String queryUpper = query.trim().toUpperCase();
            String result;
            
            // Находим первое ключевое слово запроса (SELECT, CONSTRUCT, ASK, DESCRIBE)
            // используя Pattern для поиска после директив PREFIX, BASE
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b(SELECT|CONSTRUCT|ASK|DESCRIBE)\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = pattern.matcher(query);
            String queryType = null;
            if (matcher.find()) {
                queryType = matcher.group(1).toUpperCase();
            }
            
            if ("SELECT".equals(queryType)) {
                result = sparqlService.executeSelect(query);
            } else if ("CONSTRUCT".equals(queryType)) {
                result = sparqlService.executeConstruct(query);
                format = "turtle";
            } else if ("ASK".equals(queryType)) {
                boolean askResult = sparqlService.executeAsk(query);
                result = "{\"boolean\": " + askResult + "}";
            } else if ("DESCRIBE".equals(queryType)) {
                result = sparqlService.executeDescribe(query);
                format = "turtle";
            } else {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Неподдерживаемый тип SPARQL запроса. Ожидается SELECT, CONSTRUCT, ASK или DESCRIBE.\"}");
            }
            
            HttpHeaders headers = new HttpHeaders();
            if ("json".equals(format)) {
                headers.setContentType(MediaType.APPLICATION_JSON);
            } else if ("turtle".equals(format) || "ttl".equals(format)) {
                headers.setContentType(MediaType.valueOf("text/turtle"));
            }
            
            return ResponseEntity.ok().headers(headers).body(result);
        } catch (Exception e) {
            logger.error("Ошибка выполнения SPARQL запроса", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(value = "/sparql", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, "application/sparql-query"})
    public ResponseEntity<String> sparqlQueryPost(
            @RequestParam(value = "query", required = false) String queryParam,
            @RequestBody(required = false) String queryBody,
            @RequestParam(value = "format", defaultValue = "json") String format) {
        try {
            String query = queryParam != null ? queryParam : queryBody;
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"SPARQL запрос не указан\"}");
            }
            
            logger.debug("SPARQL Query (POST): {}", query.substring(0, Math.min(100, query.length())));
            
            // Определение типа запроса (ищем первое ключевое слово, игнорируя PREFIX, BASE и т.д.)
            String queryUpper = query.trim().toUpperCase();
            String result;
            
            // Находим первое ключевое слово запроса (SELECT, CONSTRUCT, ASK, DESCRIBE)
            // используя Pattern для поиска после директив PREFIX, BASE
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b(SELECT|CONSTRUCT|ASK|DESCRIBE)\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = pattern.matcher(query);
            String queryType = null;
            if (matcher.find()) {
                queryType = matcher.group(1).toUpperCase();
            }
            
            if ("SELECT".equals(queryType)) {
                result = sparqlService.executeSelect(query);
            } else if ("CONSTRUCT".equals(queryType)) {
                result = sparqlService.executeConstruct(query);
                format = "turtle";
            } else if ("ASK".equals(queryType)) {
                boolean askResult = sparqlService.executeAsk(query);
                result = "{\"boolean\": " + askResult + "}";
            } else if ("DESCRIBE".equals(queryType)) {
                result = sparqlService.executeDescribe(query);
                format = "turtle";
            } else {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Неподдерживаемый тип SPARQL запроса. Ожидается SELECT, CONSTRUCT, ASK или DESCRIBE.\"}");
            }
            
            HttpHeaders headers = new HttpHeaders();
            if ("json".equals(format)) {
                headers.setContentType(MediaType.APPLICATION_JSON);
            } else if ("turtle".equals(format) || "ttl".equals(format)) {
                headers.setContentType(MediaType.valueOf("text/turtle"));
            }
            
            return ResponseEntity.ok().headers(headers).body(result);
        } catch (Exception e) {
            logger.error("Ошибка выполнения SPARQL запроса", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * SPARQL Update endpoint
     * 
     * POST: update в теле запроса или в form-data
     */
    @PostMapping(value = "/update", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, "application/sparql-update"})
    public ResponseEntity<String> sparqlUpdate(
            @RequestParam(value = "update", required = false) String updateParam,
            @RequestBody(required = false) String updateBody) {
        try {
            String update = updateParam != null ? updateParam : updateBody;
            if (update == null || update.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"SPARQL Update запрос не указан\"}");
            }
            
            logger.debug("SPARQL Update: {}", update.substring(0, Math.min(100, update.length())));
            
            sparqlService.executeUpdate(update);
            
            return ResponseEntity.ok().body("{\"message\": \"Update выполнен успешно\"}");
        } catch (Exception e) {
            logger.error("Ошибка выполнения SPARQL Update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Доступ к данным Named Graph
     * 
     * GET /data?graph=GRAPH_URI&format=TURTLE
     */
    @GetMapping(value = "/data", produces = {MediaType.APPLICATION_JSON_VALUE, "text/turtle", "application/rdf+xml"})
    public ResponseEntity<String> getData(
            @RequestParam(value = "graph", required = false) String graphUri,
            @RequestParam(value = "format", defaultValue = "turtle") String format) {
        try {
            if (graphUri == null || graphUri.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"URI Named Graph не указан\"}");
            }
            
            logger.debug("Получение данных из Named Graph: {} (format: {})", graphUri, format);
            
            String result = sparqlService.getGraphData(graphUri, format.toUpperCase());
            
            HttpHeaders headers = new HttpHeaders();
            if ("turtle".equalsIgnoreCase(format) || "ttl".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.valueOf("text/turtle"));
            } else if ("rdf".equalsIgnoreCase(format) || "xml".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.valueOf("application/rdf+xml"));
            } else if ("json".equalsIgnoreCase(format) || "jsonld".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_JSON);
            }
            
            return ResponseEntity.ok().headers(headers).body(result);
        } catch (Exception e) {
            logger.error("Ошибка получения данных", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Экспорт онтологии для внешних редакторов
     * 
     * GET /ontology
     * Возвращает онтологию из графа urn:ontology в формате Turtle.
     * Упрощённый endpoint без параметров для удобного использования в Protege Desktop
     * (File → Open from URL).
     */
    @GetMapping(value = "/ontology", produces = "text/turtle")
    public ResponseEntity<String> getOntology() {
        try {
            logger.debug("Экспорт онтологии из графа urn:ontology");
            
            String result = sparqlService.getGraphData("urn:ontology", "TURTLE");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("text/turtle"));
            
            return ResponseEntity.ok().headers(headers).body(result);
        } catch (Exception e) {
            logger.error("Ошибка экспорта онтологии", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("# Ошибка экспорта онтологии: " + e.getMessage() + "\n");
        }
    }

    /**
     * Импорт онтологии из файла
     * 
     * POST /ontology/import
     * Загружает онтологию из файла и заменяет содержимое графа urn:ontology.
     * Поддерживает форматы: Turtle, RDF/XML, JSON-LD.
     * 
     * @param file Файл с онтологией
     * @param format Формат файла (turtle, rdf, xml, jsonld). По умолчанию: turtle
     * @return JSON с информацией о результате импорта
     */
    @PostMapping(value = "/ontology/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importOntology(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "turtle") String format) {
        try {
            // Проверка наличия файла
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Файл не указан или пуст\"}");
            }
            
            logger.debug("Импорт онтологии из файла: {} (format: {})", file.getOriginalFilename(), format);
            
            // Загрузка данных в граф
            long tripleCount = sparqlService.replaceGraphData("urn:ontology", file.getInputStream(), format);
            
            // Формирование ответа
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode response = mapper.createObjectNode();
            response.put("status", "success");
            response.put("message", "Онтология успешно импортирована");
            response.put("triples", tripleCount);
            response.put("filename", file.getOriginalFilename());
            response.put("format", format);
            
            logger.info("Онтология успешно импортирована. Загружено триплетов: {}", tripleCount);
            
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (Exception e) {
            logger.error("Ошибка импорта онтологии", e);
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode errorResponse = mapper.createObjectNode();
                errorResponse.put("status", "error");
                errorResponse.put("error", "Ошибка импорта онтологии: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapper.writeValueAsString(errorResponse));
            } catch (Exception jsonException) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Ошибка импорта онтологии: " + e.getMessage() + "\"}");
            }
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("{\"status\": \"ok\"}");
    }
}

