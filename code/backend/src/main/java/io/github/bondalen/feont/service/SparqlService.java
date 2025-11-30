package io.github.bondalen.feont.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с SPARQL запросами и обновлениями
 * 
 * @author Александр
 * @version 1.0.0
 */
@Service
public class SparqlService {

    private static final Logger logger = LoggerFactory.getLogger(SparqlService.class);

    private final Dataset dataset;

    @Autowired
    public SparqlService(org.apache.jena.query.Dataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Выполнение SPARQL SELECT запроса
     * 
     * @param query SPARQL запрос
     * @return Результат запроса в формате JSON
     */
    public String executeSelect(String query) {
        try {
            QueryExecution qe = QueryExecutionFactory.create(query, dataset);
            ResultSet results = qe.execSelect();
            
            // Преобразование результата в JSON
            String jsonResult = ResultSetFormatter.outputAsJSONString(results);
            
            qe.close();
            logger.debug("SPARQL SELECT запрос выполнен успешно");
            return jsonResult;
        } catch (Exception e) {
            logger.error("Ошибка выполнения SPARQL SELECT запроса", e);
            throw new RuntimeException("Ошибка выполнения SPARQL запроса: " + e.getMessage(), e);
        }
    }

    /**
     * Выполнение SPARQL CONSTRUCT запроса
     * 
     * @param query SPARQL запрос
     * @return Модель RDF в формате Turtle
     */
    public String executeConstruct(String query) {
        try {
            QueryExecution qe = QueryExecutionFactory.create(query, dataset);
            Model model = qe.execConstruct();
            
            // Преобразование модели в Turtle формат
            String turtleResult = modelToString(model, "TURTLE");
            
            qe.close();
            logger.debug("SPARQL CONSTRUCT запрос выполнен успешно");
            return turtleResult;
        } catch (Exception e) {
            logger.error("Ошибка выполнения SPARQL CONSTRUCT запроса", e);
            throw new RuntimeException("Ошибка выполнения SPARQL запроса: " + e.getMessage(), e);
        }
    }

    /**
     * Выполнение SPARQL ASK запроса
     * 
     * @param query SPARQL запрос
     * @return true если запрос вернул true, иначе false
     */
    public boolean executeAsk(String query) {
        try {
            QueryExecution qe = QueryExecutionFactory.create(query, dataset);
            boolean result = qe.execAsk();
            qe.close();
            logger.debug("SPARQL ASK запрос выполнен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка выполнения SPARQL ASK запроса", e);
            throw new RuntimeException("Ошибка выполнения SPARQL запроса: " + e.getMessage(), e);
        }
    }

    /**
     * Выполнение SPARQL DESCRIBE запроса
     * 
     * @param query SPARQL запрос
     * @return Модель RDF в формате Turtle
     */
    public String executeDescribe(String query) {
        try {
            QueryExecution qe = QueryExecutionFactory.create(query, dataset);
            Model model = qe.execDescribe();
            
            String turtleResult = modelToString(model, "TURTLE");
            
            qe.close();
            logger.debug("SPARQL DESCRIBE запрос выполнен успешно");
            return turtleResult;
        } catch (Exception e) {
            logger.error("Ошибка выполнения SPARQL DESCRIBE запроса", e);
            throw new RuntimeException("Ошибка выполнения SPARQL запроса: " + e.getMessage(), e);
        }
    }

    /**
     * Выполнение SPARQL Update запроса
     * 
     * @param update SPARQL Update запрос
     */
    public void executeUpdate(String update) {
        try {
            UpdateRequest updateRequest = UpdateFactory.create(update);
            UpdateAction.execute(updateRequest, dataset);
            logger.debug("SPARQL Update запрос выполнен успешно");
        } catch (Exception e) {
            logger.error("Ошибка выполнения SPARQL Update запроса", e);
            throw new RuntimeException("Ошибка выполнения SPARQL Update: " + e.getMessage(), e);
        }
    }

    /**
     * Получение данных из указанного Named Graph
     * 
     * @param graphUri URI Named Graph
     * @param format Формат вывода (TURTLE, RDF/XML, JSON-LD)
     * @return Данные в указанном формате
     */
    public String getGraphData(String graphUri, String format) {
        try {
            Model model = dataset.getNamedModel(graphUri);
            if (model == null) {
                model = ModelFactory.createDefaultModel();
            }
            return modelToString(model, format);
        } catch (Exception e) {
            logger.error("Ошибка получения данных из Named Graph: {}", graphUri, e);
            throw new RuntimeException("Ошибка получения данных: " + e.getMessage(), e);
        }
    }

    /**
     * Преобразование модели в строку
     */
    private String modelToString(Model model, String format) {
        java.io.StringWriter writer = new java.io.StringWriter();
        model.write(writer, format);
        return writer.toString();
    }

    /**
     * Получение Dataset для прямого доступа (если необходимо)
     */
    public Dataset getDataset() {
        return dataset;
    }
}

