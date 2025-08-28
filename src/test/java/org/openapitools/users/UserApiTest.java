package org.openapitools.users;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openapitools.base.BaseApiTest;
import org.openapitools.base.TestDataProvider;
import org.openapitools.client.model.petStoreModel.User;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для User API.
 * Проверяет CRUD операции и дополнительные функции для работы с пользователями.
 */
@Feature("User")
public class UserApiTest extends BaseApiTest {
    
    private String createdUsername;
    
    @AfterEach
    void tearDown() {
        cleanup();
    }
    
    @Override
    protected void cleanup() {
        if (createdUsername != null) {
            try {
                logStep("Очистка тестовых данных");
                userRestClient.deleteUser(createdUsername);
                logData("Cleanup", "User with username " + createdUsername + " deleted");
            } catch (Exception e) {
                logError("Cleanup failed", e);
            } finally {
                createdUsername = null;
            }
        }
    }
}
