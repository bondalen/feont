package io.github.bondalen.feont.config;

/**
 * Константы для RDF префиксов и URI онтологии FEONT
 * 
 * @author Александр
 * @version 1.0.0
 */
public class OntologyConstants {
    
    /**
     * Базовый URI онтологии FEONT
     * Используется для всех классов и свойств проекта
     */
    public static final String FEONT_NS = "https://feont.ontoline.ru/ontology/";
    
    /**
     * HTTP версия для локальной разработки без SSL
     */
    public static final String FEONT_NS_HTTP = "http://feont.ontoline.ru/ontology/";
    
    // Классы
    public static final String Department = FEONT_NS + "Department";
    public static final String Employee = FEONT_NS + "Employee";
    public static final String System = FEONT_NS + "System";
    public static final String Component = FEONT_NS + "Component";
    public static final String NamedGraph = FEONT_NS + "NamedGraph";
    
    // Свойства
    public static final String name = FEONT_NS + "name";
    public static final String worksIn = FEONT_NS + "worksIn";
    public static final String partOf = FEONT_NS + "partOf";
    public static final String code = FEONT_NS + "code";
    public static final String version = FEONT_NS + "version";
    
    private OntologyConstants() {
        // Утилитный класс
    }
}

