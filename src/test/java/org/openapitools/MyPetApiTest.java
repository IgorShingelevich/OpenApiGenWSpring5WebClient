package org.openapitools;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.petStoreApi.PetApi;
import org.openapitools.client.model.petStoreModel.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MyPetApiTest {

    private static final Logger logger = LoggerFactory.getLogger(MyPetApiTest.class);

    private final PetApi petApi = new PetApi();

    @Test
    @Description("Test getting a pet by ID")
    public void getPetByIdTest() {
        Long petId = 1L;

        Allure.step("Arrange", () -> {
            logger.info("Getting pet with ID: {}", petId);
        });

        Pet pet = Allure.step("Act", () -> {
            Pet retrievedPet = petApi.getPetById(petId).block();
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
            petApi.addPet(newPet).block();
            logger.info("Added pet: {}", newPet);
            logger.info("Getting the added pet by name: {}", newPet.getName());
            List<Pet> pets = petApi.findPetsByStatus(Arrays.asList(newPet.getStatus().getValue())).collectList().block();
            Pet retrievedPet = pets.stream().filter(pet -> pet.getName().equals(newPet.getName())).findFirst().orElse(null);
            Allure.addAttachment("Added Pet", retrievedPet.toString());
            return retrievedPet;
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
            petApi.addPet(petToDelete).block();
            logger.info("Added pet to be deleted: {}", petToDelete);
            List<Pet> pets = petApi.findPetsByStatus(Arrays.asList(petToDelete.getStatus().getValue())).collectList().block();
            Pet addedPet = pets.stream().filter(pet -> pet.getName().equals(petToDelete.getName())).findFirst().orElse(null);
            petToDelete.setId(addedPet.getId());
            logger.info("Retrieved added pet: {}", addedPet);
            Allure.addAttachment("Pet to Delete", petToDelete.toString());
        });

        Allure.step("Act", () -> {
            logger.info("Getting the added pet by ID to ensure it exists: {}", petToDelete.getId());
            Pet existingPet = petApi.getPetById(petToDelete.getId()).block();
            assertThat(existingPet).isNotNull();

            logger.info("Deleting the pet with ID: {}", petToDelete.getId());
            petApi.deletePet(petToDelete.getId(), null).block();
            Allure.addAttachment("Deleted Pet ID", petToDelete.getId().toString());
        });

        Allure.step("Assert", () -> {
            logger.info("Attempting to get the deleted pet by ID: {}", petToDelete.getId());
            Pet deletedPet = petApi.getPetById(petToDelete.getId())
                    .onErrorResume(error -> Mono.empty())
                    .block();
            Allure.addAttachment("Deleted Pet", deletedPet != null ? deletedPet.toString() : "null");

            logger.info("Asserting the deleted pet is null");
            assertThat(deletedPet).isNull();
        });
    }
}