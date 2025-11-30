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
        // Статические ресурсы frontend
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true);
    }

    /**
     * Настройка fallback на index.html для SPA роутинга
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Все не-SPARQL запросы перенаправляются на index.html для SPA
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}

