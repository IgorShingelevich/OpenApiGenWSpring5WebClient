package org.openapitools.pets;

import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openapitools.base.BaseApiTest;
import org.openapitools.base.TestDataProvider;
import org.openapitools.client.model.petStoreModel.Pet;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для Pet API.
 * Проверяет CRUD операции и дополнительные функции для работы с питомцами.
 */
public class PetApiTest extends BaseApiTest {
    
    private Long createdPetId;
    
    @AfterEach
    void tearDown() {
        cleanup();
    }
    
    @Test
    @Description("Тест поиска питомцев по статусу")
    public void testFindPetsByStatus() {
        logStep("Поиск питомцев со статусом AVAILABLE");
        List<Pet> availablePets = petRestClient.findPetsByStatus(Pet.StatusEnum.AVAILABLE);
        
        logStep("Проверка результатов поиска");
        assertThat(availablePets).isNotNull();
        
        if (!availablePets.isEmpty()) {
            availablePets.forEach(pet -> 
                assertThat(pet.getStatus()).isEqualTo(Pet.StatusEnum.AVAILABLE)
            );
        }
        
        logData("Available Pets Count", availablePets.size());
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
