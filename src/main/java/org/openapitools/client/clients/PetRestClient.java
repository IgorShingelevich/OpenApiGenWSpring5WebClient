package org.openapitools.client.clients;

import org.openapitools.client.RestClient;
import org.openapitools.client.model.petStoreModel.Pet;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * REST клиент для работы с Pet API.
 * Предоставляет типизированные методы для CRUD операций с питомцами.
 */
@Component
public class PetRestClient extends BaseRestClient {
    
    private static final String PET_ENDPOINT = "pet";
    private static final String PET_BY_STATUS_ENDPOINT = "pet/findByStatus";
    private static final String PET_BY_TAGS_ENDPOINT = "pet/findByTags";
    private static final String PET_UPLOAD_IMAGE_ENDPOINT = "pet/{petId}/uploadImage";
    
    public PetRestClient(RestClient restClient) {
        super(restClient);
    }
    
    /**
     * Добавить нового питомца
     */
    public Pet createPet(Pet pet) {
        return restClient.post(pet, Pet.class, PET_ENDPOINT);
    }
    
    /**
     * Получить питомца по ID
     */
    public Pet getPetById(Long petId) {
        return restClient.get(Pet.class, PET_ENDPOINT, petId);
    }
    
    /**
     * Обновить питомца
     */
    public Pet updatePet(Pet pet) {
        return restClient.put(pet, Pet.class, PET_ENDPOINT);
    }
    
    /**
     * Удалить питомца по ID
     */
    public void deletePet(Long petId) {
        restClient.delete(Void.class, PET_ENDPOINT, petId);
    }
    
    /**
     * Найти питомцев по статусу
     */
    public List<Pet> findPetsByStatus(Pet.StatusEnum status) {
        List<String> queryParams = java.util.Arrays.asList("status=" + status.getValue());
        return restClient.get(List.class, queryParams, "pet", "findByStatus");
    }
    
    /**
     * Найти питомцев по тегам
     */
    public List<Pet> findPetsByTags(List<String> tags) {
        String tagsParam = String.join(",", tags);
        List<String> queryParams = java.util.Arrays.asList("tags=" + tagsParam);
        return restClient.get(List.class, queryParams, "pet", "findByTags");
    }
}
