package com.dh.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/hotel-images/**")
                .addResourceLocations("file:uploads/hotel-images/")
                .setCachePeriod(3600); // Cache de 1 hora

        System.out.println("✅ Configuración de archivos estáticos aplicada");
        System.out.println("📁 Sirviendo archivos desde: uploads/hotel-images/");
    }
}