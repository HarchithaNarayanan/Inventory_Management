package com.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig — Cross-Origin Resource Sharing (CORS) configuration.
 *
 * <p>Allows the REST APIs to be called from a front-end application
 * running on a different origin (e.g., React/Angular dev server on port 3000).</p>
 *
 * <p>During development, all origins are permitted. For production,
 * replace {@code *} with the actual front-end domain.</p>
 */
@Configuration
public class CorsConfig {

    /**
     * Registers global CORS mappings that apply to all controller endpoints.
     *
     * <p>Configuration:</p>
     * <ul>
     *   <li>Path pattern: all routes under {@code /api/**}</li>
     *   <li>Allowed origins: all (development mode)</li>
     *   <li>Allowed HTTP methods: GET, POST, PUT, DELETE, OPTIONS</li>
     *   <li>Allowed headers: all headers</li>
     *   <li>Exposed headers: none (can add Authorization if needed)</li>
     *   <li>Max age: 3600 seconds (preflight cache)</li>
     * </ul>
     *
     * @return a configured {@link WebMvcConfigurer} bean
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .maxAge(3600);
            }
        };
    }
}
