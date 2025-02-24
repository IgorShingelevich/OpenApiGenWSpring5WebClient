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
        Pet newPet = new Pet()
                .name("TestPet")
                .photoUrls(Arrays.asList("https://example.com/testpet.jpg"))
                .status(Pet.StatusEnum.AVAILABLE);
        final Long[] petId = new Long[1];

        Allure.step("Arrange", () -> {
            logger.info("Creating a new pet for testing");
            Pet createdPet = restClient.post(newPet, Pet.class, "pet");
            petId[0] = createdPet.getId();
            logger.info("Created pet with ID: {}", petId[0]);
        });

        Pet pet = Allure.step("Act", () -> {
            logger.info("Getting pet with ID: {}", petId[0]);
            Pet retrievedPet = restClient.get(Pet.class, "pet", petId[0]);
            Allure.addAttachment("Retrieved Pet", retrievedPet.toString());
            return retrievedPet;
        });

        Allure.step("Assert", () -> {
            logger.info("Asserting pet ID matches the requested ID");
            assertThat(pet.getId()).isEqualTo(petId[0]);
            assertThat(pet.getName()).isEqualTo(newPet.getName());
            assertThat(pet.getStatus()).isEqualTo(newPet.getStatus());
        });

        // Cleanup
        Allure.step("Cleanup", () -> {
            logger.info("Cleaning up - deleting test pet");
            restClient.delete(Pet.class, "pet", petId[0]);
        });
    }

    @Test
    @Description("Test adding a new pet and verifying its details")
    public void addPetTest() {
        Pet newPet = new Pet()
                .name("Fluffy")
                .photoUrls(Arrays.asList("https://example.com/fluffy.jpg"))
                .status(Pet.StatusEnum.AVAILABLE);
        final Long[] petId = new Long[1];

        Allure.step("Arrange", () -> {
            logger.info("Adding new pet: {}", newPet);
            Allure.addAttachment("New Pet", newPet.toString());
        });

        Pet addedPet = Allure.step("Act", () -> {
            Pet createdPet = restClient.post(newPet, Pet.class, "pet");
            petId[0] = createdPet.getId();
            logger.info("Added pet with ID: {}", petId[0]);
            logger.info("Getting the added pet by status: {}", newPet.getStatus().getValue());
            List<String> queryParams = Arrays.asList("status=" + newPet.getStatus().getValue());
            Pet[] pets = restClient.get(Pet[].class, queryParams, "pet", "findByStatus");
            return Arrays.stream(pets)
                    .filter(pet -> pet.getName() != null && pet.getName().equals(newPet.getName()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Could not find the created pet by status"));
        });

        Allure.step("Assert", () -> {
            logger.info("Asserting the added pet details match the original pet");
            assertThat(addedPet.getName()).isEqualTo(newPet.getName());
            assertThat(addedPet.getPhotoUrls()).isEqualTo(newPet.getPhotoUrls());
            assertThat(addedPet.getStatus()).isEqualTo(newPet.getStatus());
        });

        // Cleanup
        Allure.step("Cleanup", () -> {
            logger.info("Cleaning up - deleting test pet");
            restClient.delete(Pet.class, "pet", petId[0]);
        });
    }

    @Test
    @Description("Test deleting a pet")
    public void deletePetTest() {
        Pet petToDelete = new Pet()
                .name("Buddy")
                .photoUrls(Arrays.asList("https://example.com/buddy.jpg"))
                .status(Pet.StatusEnum.AVAILABLE);
        final Long[] petId = new Long[1];

        Allure.step("Arrange", () -> {
            logger.info("Adding pet to be deleted: {}", petToDelete);
            Pet addedPet = restClient.post(petToDelete, Pet.class, "pet");
            petId[0] = addedPet.getId();
            logger.info("Added pet with ID: {}", petId[0]);
            Allure.addAttachment("Pet to Delete", addedPet.toString());
        });

        Allure.step("Act", () -> {
            logger.info("Deleting the pet with ID: {}", petId[0]);
            restClient.delete(Pet.class, "pet", petId[0]);
            Allure.addAttachment("Deleted Pet ID", petId[0].toString());
        });

        Allure.step("Assert", () -> {
            logger.info("Verifying the pet was deleted by attempting to retrieve it");
            try {
                restClient.get(Pet.class, "pet", petId[0]);
                org.junit.jupiter.api.Assertions.fail("Pet should not exist after deletion");
            } catch (Exception e) {
                logger.info("Pet was successfully deleted - received expected error: {}", e.getMessage());
            }
        });
    }
}