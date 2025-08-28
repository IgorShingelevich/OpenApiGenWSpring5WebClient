package org.openapitools.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация WebClient для тестирования API.
 * Предоставляет настроенный WebClient с базовым URL и таймаутами.
 * 
 * Обновлено для работы с локальным PetStore v3 API с PostgreSQL
 */
@Configuration
public class WebClientConfig {
    
    // Локальный PetStore v3 API с PostgreSQL
    private static final String BASE_URL = "http://localhost:8080/api/v3";
    private static final int CONNECT_TIMEOUT = 10000; // 10 секунд
    private static final int READ_TIMEOUT = 30000; // 30 секунд
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }
    
    /**
     * Получить базовый URL для API
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }
    
    /**
     * Получить таймаут подключения
     */
    public static int getConnectTimeout() {
        return CONNECT_TIMEOUT;
    }
    
    /**
     * Получить таймаут чтения
     */
    public static int getReadTimeout() {
        return READ_TIMEOUT;
    }
}
