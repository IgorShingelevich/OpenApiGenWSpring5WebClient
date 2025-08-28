package org.openapitools.client.clients;

import org.openapitools.client.RestClient;
import org.springframework.stereotype.Component;

/**
 * Абстрактный базовый класс для всех REST клиентов.
 * Предоставляет общую функциональность и типизированные методы.
 */
@Component
public abstract class BaseRestClient {
    
    protected final RestClient restClient;
    
    public BaseRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
    
    /**
     * Получить базовый REST клиент
     */
    protected RestClient getRestClient() {
        return restClient;
    }
}
