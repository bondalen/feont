package io.github.bondalen.feont.service;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Сервис инициализации Named Graphs при старте приложения
 * 
 * @author Александр
 * @version 1.0.0
 */
@Service
public class DatasetInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(DatasetInitializationService.class);

    private final Dataset dataset;

    @Value("${feont.named-graphs.ontology:urn:ontology}")
    private String ontologyGraphUri;

    @Value("${feont.named-graphs.data:urn:data}")
    private String dataGraphUri;

    @Value("${feont.named-graphs.shacl:urn:shacl:shapes}")
    private String shaclGraphUri;

    @Value("${feont.named-graphs.history:urn:ontology:history}")
    private String historyGraphUri;

    @Value("${feont.named-graphs.validation:urn:validation:results}")
    private String validationGraphUri;

    @Autowired
    public DatasetInitializationService(Dataset dataset) {
        this.dataset = dataset;
    }

    @PostConstruct
    public void initializeNamedGraphs() {
        logger.info("Инициализация Named Graphs...");

        // Инициализация онтологии
        initializeGraph(ontologyGraphUri, "Онтология");
        
        // Инициализация данных
        initializeGraph(dataGraphUri, "Данные");
        
        // Инициализация SHACL shapes
        initializeGraph(shaclGraphUri, "SHACL shapes");
        
        // Инициализация истории онтологии
        initializeGraph(historyGraphUri, "История онтологии");
        
        // Инициализация результатов валидации
        initializeGraph(validationGraphUri, "Результаты валидации");

        logger.info("Named Graphs инициализированы успешно");
    }

    /**
     * Инициализация Named Graph
     * 
     * TDB2 требует использования транзакций для операций записи.
     * 
     * @param graphUri URI графа
     * @param description Описание графа
     */
    private void initializeGraph(String graphUri, String description) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getNamedModel(graphUri);
            if (model.isEmpty()) {
                // Добавляем базовую информацию о графе
                Resource graphResource = model.createResource(graphUri);
                model.add(graphResource, RDF.type, model.createResource("http://example.org/feont/NamedGraph"));
                model.add(graphResource, 
                    model.createProperty("http://www.w3.org/2000/01/rdf-schema#label"),
                    description);
                
                dataset.commit();
                logger.debug("Инициализирован Named Graph: {} ({})", graphUri, description);
            } else {
                dataset.commit();
                logger.debug("Named Graph уже существует: {} ({})", graphUri, description);
            }
        } catch (Exception e) {
            dataset.abort();
            logger.error("Ошибка инициализации Named Graph: {}", graphUri, e);
        } finally {
            dataset.end();
        }
    }
}

