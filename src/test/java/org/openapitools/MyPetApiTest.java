package org.openapitools;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;
import org.openapitools.client.model.petStoreModel.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MyPetApiTest {

    private static final Logger logger = LoggerFactory.getLogger(MyPetApiTest.class);
    private final RestClient restClient;

    public MyPetApiTest() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://petstore.swagger.io/v2")
                .build();
        this.restClient = new RestClient(webClient);
    }

    @Test
    @Description("Test getting a pet by ID")
    public void getPetByIdTest() {
        Long petId = 1L;

        Allure.step("Arrange", () -> {
            logger.info("Getting pet with ID: {}", petId);
        });

        Pet pet = Allure.step("Act", () -> {
            Pet retrievedPet = restClient.get(Pet.class, "pet", petId);
            Allure.addAttachment("Retrieved Pet", retrievedPet.toString());
            return retrievedPet;
        });

        Allure.step("Assert", () -> {
            logger.info("Asserting pet ID matches the requested ID");
            assertThat(pet.getId()).isEqualTo(petId);
        });
    }

    @Test
    @Description("Test adding a new pet and verifying its details")
    public void addPetTest() {
        Pet newPet = new Pet()
                .name("Fluffy")
                .photoUrls(Arrays.asList("https://example.com/fluffy.jpg"))
                .status(Pet.StatusEnum.AVAILABLE);

        Allure.step("Arrange", () -> {
            logger.info("Adding new pet: {}", newPet);
            Allure.addAttachment("New Pet", newPet.toString());
        });

        Pet addedPet = Allure.step("Act", () -> {
            restClient.post(newPet, Pet.class, "pet");
            logger.info("Added pet: {}", newPet);
            logger.info("Getting the added pet by status: {}", newPet.getStatus().getValue());
            List<String> queryParams = Arrays.asList("status=" + newPet.getStatus().getValue());
            Pet[] pets = restClient.get(Pet[].class, queryParams, "pet", "findByStatus");
            return Arrays.stream(pets)
                    .filter(pet -> pet.getName() != null && pet.getName().equals(newPet.getName()))
                    .findFirst()
                    .orElse(null);
        });

        Allure.step("Assert", () -> {
            logger.info("Asserting the added pet details match the original pet");
            assertThat(addedPet.getName()).isEqualTo(newPet.getName());
            assertThat(addedPet.getPhotoUrls()).isEqualTo(newPet.getPhotoUrls());
            assertThat(addedPet.getStatus()).isEqualTo(newPet.getStatus());
        });
    }

    @Test
    @Description("Test deleting a pet")
    public void deletePetTest() {
        Pet petToDelete = new Pet()
                .name("Buddy")
                .photoUrls(Arrays.asList("https://example.com/buddy.jpg"))
                .status(Pet.StatusEnum.AVAILABLE);

        Allure.step("Arrange", () -> {
            logger.info("Adding pet to be deleted: {}", petToDelete);
            restClient.post(petToDelete, Pet.class, "pet");
            logger.info("Added pet to be deleted: {}", petToDelete);
            List<String> queryParams = Arrays.asList("status=" + petToDelete.getStatus().getValue());
            Pet[] pets = restClient.get(Pet[].class, queryParams, "pet", "findByStatus");
            Pet addedPet = Arrays.stream(pets)
                    .filter(pet -> pet.getName() != null && pet.getName().equals(petToDelete.getName()))
                    .findFirst()
                    .orElse(null);
            if (addedPet != null) {
                petToDelete.setId(addedPet.getId());
                logger.info("Retrieved added pet: {}", addedPet);
                Allure.addAttachment("Pet to Delete", petToDelete.toString());
            } else {
                logger.error("Could not find the added pet");
            }
        });

        Allure.step("Act", () -> {
            logger.info("Getting the added pet by ID to ensure it exists: {}", petToDelete.getId());
            Pet existingPet = restClient.get(Pet.class, "pet", petToDelete.getId());
            assertThat(existingPet).isNotNull();

            logger.info("Deleting the pet with ID: {}", petToDelete.getId());
            restClient.delete(Pet.class, "pet", petToDelete.getId());
            Allure.addAttachment("Deleted Pet ID", petToDelete.getId().toString());
        });

        Allure.step("Assert", () -> {
            logger.info("Attempting to get the deleted pet by ID: {}", petToDelete.getId());
            try {
                Pet deletedPet = restClient.get(Pet.class, "pet", petToDelete.getId());
                assertThat(deletedPet).isNull();
            } catch (Exception e) {
                // Expected behavior - pet should not exist
                logger.info("Pet was successfully deleted");
            }
        });
    }
}