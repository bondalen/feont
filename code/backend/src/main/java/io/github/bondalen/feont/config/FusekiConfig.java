package io.github.bondalen.feont.config;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.tdb2.TDB2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Конфигурация Apache Jena Fuseki
 * 
 * Настраивает встроенный Fuseki сервер с подключением к TDB2 хранилищу
 * 
 * @author Александр
 * @version 1.0.0
 */
@Configuration
public class FusekiConfig {

    private static final Logger logger = LoggerFactory.getLogger(FusekiConfig.class);

    @Value("${feont.tdb2.path}")
    private String tdb2Path;

    @Value("${feont.fuseki.dataset-name:ds}")
    private String datasetName;

    /**
     * Создание TDB2 Dataset
     * 
     * @return Dataset подключенный к TDB2 хранилищу
     */
    @Bean
    public Dataset dataset() {
        Path path = Paths.get(tdb2Path).toAbsolutePath();
        logger.info("Инициализация TDB2 хранилища по пути: {}", path);
        
        // Создание директории, если её нет
        path.toFile().mkdirs();
        
        Dataset dataset = TDB2Factory.connectDataset(path.toString());
        logger.info("TDB2 хранилище успешно подключено");
        
        return dataset;
    }

    /**
     * Инициализация встроенного Fuseki сервера
     * 
     * @param dataset Dataset для использования
     * @return FusekiServer (будет запущен отдельно или через Servlet)
     */
    @Bean
    public FusekiServer fusekiServer(Dataset dataset) {
        logger.info("Инициализация встроенного Fuseki сервера с dataset: {}", datasetName);
        
        // TODO: Настройка Fuseki сервера
        // FusekiServer будет интегрирован через Spring Boot REST контроллеры
        // для экономии ресурсов вместо отдельного сервера
        
        logger.info("Fuseki сервер настроен");
        return null; // Временно, будет реализовано позже
    }
}

