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
            @RequestParam("query") String query,
            @RequestParam(value = "format", defaultValue = "json") String format) {
        try {
            logger.debug("SPARQL Query (GET): {}", query.substring(0, Math.min(100, query.length())));
            
            // Определение типа запроса
            String queryUpper = query.trim().toUpperCase();
            String result;
            
            if (queryUpper.startsWith("SELECT")) {
                result = sparqlService.executeSelect(query);
            } else if (queryUpper.startsWith("CONSTRUCT")) {
                result = sparqlService.executeConstruct(query);
                format = "turtle";
            } else if (queryUpper.startsWith("ASK")) {
                boolean askResult = sparqlService.executeAsk(query);
                result = "{\"boolean\": " + askResult + "}";
            } else if (queryUpper.startsWith("DESCRIBE")) {
                result = sparqlService.executeDescribe(query);
                format = "turtle";
            } else {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Неподдерживаемый тип SPARQL запроса\"}");
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

    @PostMapping(value = "/sparql", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_SPARQL_QUERY})
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
            
            // Определение типа запроса
            String queryUpper = query.trim().toUpperCase();
            String result;
            
            if (queryUpper.startsWith("SELECT")) {
                result = sparqlService.executeSelect(query);
            } else if (queryUpper.startsWith("CONSTRUCT")) {
                result = sparqlService.executeConstruct(query);
                format = "turtle";
            } else if (queryUpper.startsWith("ASK")) {
                boolean askResult = sparqlService.executeAsk(query);
                result = "{\"boolean\": " + askResult + "}";
            } else if (queryUpper.startsWith("DESCRIBE")) {
                result = sparqlService.executeDescribe(query);
                format = "turtle";
            } else {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Неподдерживаемый тип SPARQL запроса\"}");
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
     * Health check endpoint
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("{\"status\": \"ok\"}");
    }
}

