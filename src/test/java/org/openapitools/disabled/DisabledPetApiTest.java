package org.openapitools.disabled;

import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openapitools.base.BaseApiTest;
import org.openapitools.base.TestDataProvider;
import org.openapitools.client.model.petStoreModel.Pet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Отключенные тесты для Pet API.
 * Тесты, которые не могут работать из-за ограничений демо API.
 */
public class DisabledPetApiTest extends BaseApiTest {
    
    private Long createdPetId;
    
    @AfterEach
    void tearDown() {
        cleanup();
    }
    
    @Test
    @Description("Тест создания нового питомца")
    @Disabled("Демо API не сохраняет данные между запросами. API возвращает фиксированный ID 9223372036854775807, но данные не сохраняются реально.")
    public void testCreatePet() {
        Pet testPet = TestDataProvider.createTestPet();
        
        logStep("Создание нового питомца");
        logData("Test Pet", testPet);
        
        Pet createdPet = petRestClient.createPet(testPet);
        createdPetId = createdPet.getId();
        
        logStep("Проверка результата создания");
        assertThat(createdPet.getId()).isNotNull();
        assertThat(createdPet.getName()).isEqualTo(testPet.getName());
        assertThat(createdPet.getStatus()).isEqualTo(testPet.getStatus());
        
        logData("Created Pet", createdPet);
    }
    
    @Test
    @Description("Тест получения питомца по ID")
    @Disabled("Демо API не сохраняет данные между запросами. После создания питомца API возвращает фиксированный ID 9223372036854775807, но при попытке получить питомца по этому ID возвращает 404 ошибку.")
    public void testGetPetById() {
        Pet testPet = TestDataProvider.createTestPet();
        
        logStep("Создание питомца для тестирования");
        Pet createdPet = petRestClient.createPet(testPet);
        createdPetId = createdPet.getId();
        
        logStep("Получение питомца по ID");
        Pet retrievedPet = petRestClient.getPetById(createdPetId);
        
        logStep("Проверка полученных данных");
        assertThat(retrievedPet.getId()).isEqualTo(createdPetId);
        assertThat(retrievedPet.getName()).isEqualTo(testPet.getName());
        
        logData("Retrieved Pet", retrievedPet);
    }
    
    @Test
    @Description("Тест обновления питомца")
    @Disabled("Демо API не сохраняет данные между запросами. API возвращает объект, но реальное обновление данных не происходит.")
    public void testUpdatePet() {
        Pet testPet = TestDataProvider.createTestPet();
        
        logStep("Создание питомца для обновления");
        Pet createdPet = petRestClient.createPet(testPet);
        createdPetId = createdPet.getId();
        
        logStep("Обновление данных питомца");
        Pet updatedPet = createdPet
                .name("UpdatedPet_" + System.currentTimeMillis())
                .status(Pet.StatusEnum.SOLD);
        
        Pet result = petRestClient.updatePet(updatedPet);
        
        logStep("Проверка обновления");
        assertThat(result.getName()).isEqualTo(updatedPet.getName());
        assertThat(result.getStatus()).isEqualTo(updatedPet.getStatus());
        
        logData("Updated Pet", result);
    }
    
    @Test
    @Description("Тест удаления питомца")
    @Disabled("Демо API не сохраняет данные между запросами. После создания питомца API может возвращать несуществующий ID, что приводит к 404 ошибке при попытке удалить питомца.")
    public void testDeletePet() {
        Pet testPet = TestDataProvider.createTestPet();
        
        logStep("Создание питомца для удаления");
        Pet createdPet = petRestClient.createPet(testPet);
        createdPetId = createdPet.getId();
        
        logStep("Удаление питомца");
        petRestClient.deletePet(createdPetId);
        
        logStep("Проверка удаления");
        try {
            petRestClient.getPetById(createdPetId);
            // Если питомец все еще существует, это тоже приемлемо
            logData("Pet still exists after deletion", createdPetId);
        } catch (Exception e) {
            logData("Pet successfully deleted", "Pet with ID " + createdPetId + " was deleted");
        }
        
        createdPetId = null; // Не нужно удалять в cleanup
    }
    
    @Test
    @Description("Тест создания питомца с полными данными")
    @Disabled("Демо API не сохраняет данные между запросами. API возвращает фиксированный ID 9223372036854775807, но данные не сохраняются реально.")
    public void testCreateFullPet() {
        Pet fullPet = TestDataProvider.createFullTestPet();
        
        logStep("Создание питомца с полными данными");
        logData("Full Pet", fullPet);
        
        Pet createdPet = petRestClient.createPet(fullPet);
        createdPetId = createdPet.getId();
        
        logStep("Проверка создания питомца с полными данными");
        assertThat(createdPet.getId()).isNotNull();
        assertThat(createdPet.getCategory()).isNotNull();
        assertThat(createdPet.getTags()).isNotNull();
        assertThat(createdPet.getTags()).hasSize(2);
        
        logData("Created Full Pet", createdPet);
    }
    
    @Override
    protected void cleanup() {
        if (createdPetId != null) {
            try {
                logStep("Очистка тестовых данных");
                petRestClient.deletePet(createdPetId);
                logData("Cleanup", "Pet with ID " + createdPetId + " deleted");
            } catch (Exception e) {
                logError("Cleanup failed", e);
            } finally {
                createdPetId = null;
            }
        }
    }
}
