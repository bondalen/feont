package io.github.bondalen.feont.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация веб-слоя
 * 
 * Настраивает раздачу статических файлов frontend и обработку SPA роутинга
 * 
 * @author Александр
 * @version 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настройка обработки статических ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // SPARQL endpoints имеют приоритет
        // Статические ресурсы frontend для остальных запросов
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true);
    }

    /**
     * Настройка fallback на index.html для SPA роутинга
     * 
     * Примечание: SPARQL endpoints (/ds/**) обрабатываются контроллерами,
     * остальные запросы перенаправляются на index.html
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Только корневой путь перенаправляется на index.html
        // SPARQL endpoints обрабатываются через SparqlController
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}

