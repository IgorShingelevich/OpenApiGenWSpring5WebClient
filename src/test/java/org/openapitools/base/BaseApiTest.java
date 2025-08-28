package org.openapitools.base;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.openapitools.client.RestClient;
import org.openapitools.client.clients.PetRestClient;
import org.openapitools.client.clients.UserRestClient;
import org.openapitools.client.config.WebClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Базовый класс для всех API тестов.
 * Предоставляет общую функциональность, логирование и настройку клиентов.
 */
public abstract class BaseApiTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);
    
    protected RestClient restClient;
    protected PetRestClient petRestClient;
    protected UserRestClient userRestClient;
    
    @BeforeEach
    void setUp() {
        logger.info("Setting up test environment");
        
        // Создаем WebClient с конфигурацией
        WebClient webClient = WebClient.builder()
                .baseUrl(WebClientConfig.getBaseUrl())
                .build();
        
        // Инициализируем клиенты
        this.restClient = new RestClient(webClient);
        this.petRestClient = new PetRestClient(restClient);
        this.userRestClient = new UserRestClient(restClient);
        
        logger.info("Test environment setup completed");
    }
    
    /**
     * Логирование шага теста с Allure
     */
    protected void logStep(String stepName) {
        logger.info("Step: {}", stepName);
        Allure.step(stepName);
    }
    
    /**
     * Логирование данных с Allure
     */
    protected void logData(String dataName, Object data) {
        logger.info("Data: {} = {}", dataName, data);
        Allure.addAttachment(dataName, data.toString());
    }
    
    /**
     * Логирование ошибки
     */
    protected void logError(String message, Exception e) {
        logger.error("Error: {} - {}", message, e.getMessage());
        Allure.addAttachment("Error", message + ": " + e.getMessage());
    }
    
    /**
     * Очистка тестовых данных
     */
    protected abstract void cleanup();
}
