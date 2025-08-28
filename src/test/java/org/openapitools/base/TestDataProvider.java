package org.openapitools.base;

import org.openapitools.client.model.petStoreModel.Category;
import org.openapitools.client.model.petStoreModel.Pet;
import org.openapitools.client.model.petStoreModel.Tag;
import org.openapitools.client.model.petStoreModel.User;

import java.util.Arrays;
import java.util.List;

/**
 * Провайдер тестовых данных для API тестов.
 * Предоставляет методы для создания тестовых объектов Pet и User.
 */
public class TestDataProvider {
    
    /**
     * Создать тестового питомца с базовыми данными
     */
    public static Pet createTestPet() {
        Category category = new Category()
                .id(1L)
                .name("Dogs");
        
        return new Pet()
                .name("TestPet_" + System.currentTimeMillis())
                .category(category)
                .photoUrls(Arrays.asList("https://example.com/testpet.jpg"))
                .status(Pet.StatusEnum.AVAILABLE);
    }
    
    /**
     * Создать тестового питомца с полными данными
     */
    public static Pet createFullTestPet() {
        Category category = new Category()
                .id(1L)
                .name("Dogs");
        
        Tag tag1 = new Tag()
                .id(1L)
                .name("friendly");
        
        Tag tag2 = new Tag()
                .id(2L)
                .name("playful");
        
        return new Pet()
                .name("Fluffy_" + System.currentTimeMillis())
                .category(category)
                .photoUrls(Arrays.asList(
                        "https://example.com/fluffy1.jpg",
                        "https://example.com/fluffy2.jpg"
                ))
                .tags(Arrays.asList(tag1, tag2))
                .status(Pet.StatusEnum.AVAILABLE);
    }
    
    /**
     * Создать тестового питомца с указанным статусом
     */
    public static Pet createTestPetWithStatus(Pet.StatusEnum status) {
        Category category = new Category()
                .id(1L)
                .name("Dogs");
        
        return new Pet()
                .name("TestPet_" + status.getValue() + "_" + System.currentTimeMillis())
                .category(category)
                .photoUrls(Arrays.asList("https://example.com/testpet.jpg"))
                .status(status);
    }
    
    /**
     * Создать тестового пользователя с базовыми данными
     */
    public static User createTestUser() {
        return new User()
                .username("testuser_" + System.currentTimeMillis())
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .phone("+1234567890")
                .userStatus(1);
    }
    
    /**
     * Создать тестового пользователя с полными данными
     */
    public static User createFullTestUser() {
        return new User()
                .id(1L)
                .username("fulluser_" + System.currentTimeMillis())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .phone("+1-555-123-4567")
                .userStatus(1);
    }
    
    /**
     * Создать список тестовых питомцев
     */
    public static List<Pet> createTestPets(int count) {
        Pet[] pets = new Pet[count];
        for (int i = 0; i < count; i++) {
            Category category = new Category()
                    .id((long) (i % 3 + 1))
                    .name(i % 3 == 0 ? "Dogs" : i % 3 == 1 ? "Cats" : "Birds");
            
            pets[i] = new Pet()
                    .name("BatchPet_" + i + "_" + System.currentTimeMillis())
                    .category(category)
                    .photoUrls(Arrays.asList("https://example.com/batch" + i + ".jpg"))
                    .status(Pet.StatusEnum.AVAILABLE);
        }
        return Arrays.asList(pets);
    }
    
    /**
     * Создать список тестовых пользователей
     */
    public static List<User> createTestUsers(int count) {
        User[] users = new User[count];
        for (int i = 0; i < count; i++) {
            users[i] = new User()
                    .username("batchuser_" + i + "_" + System.currentTimeMillis())
                    .firstName("User" + i)
                    .lastName("Batch")
                    .email("user" + i + "@example.com")
                    .password("password" + i)
                    .phone("+123456789" + i)
                    .userStatus(1);
        }
        return Arrays.asList(users);
    }
}
