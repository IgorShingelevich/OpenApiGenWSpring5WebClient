package org.openapitools.techniques;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openapitools.base.BaseApiTest;
import org.openapitools.client.model.petStoreModel.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Equivalence Partitioning Test Design Technique Demonstration
 * 
 * Equivalence Partitioning divides input data into partitions where all values
 * in a partition should behave similarly. We test one representative value from each partition.
 * 
 * For User API, we'll create partitions based on:
 * - Valid vs Invalid data formats
 * - Different user types/categories
 * - Different operational contexts
 */
@Feature("Test design technique: Equivalence Partitioning")
public class EquivalencePartitioningTest extends BaseApiTest {
    
    private final List<String> createdUsernames = new ArrayList<>();
    
    @AfterEach
    void tearDown() {
        cleanup();
    }
    
    @Test
    @Description("EP: Valid Email Format Partition - Standard business email")
    public void testValidEmailPartition_BusinessEmail() {
        logStep("Testing Valid Email Partition: Business email format");
        
        User user = new User()
                .username("biz_" + System.currentTimeMillis())
                .firstName("Business")
                .lastName("User")
                .email("business.user@company.com") // Valid business email partition
                .password("securePassword123")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getEmail()).isEqualTo("business.user@company.com");
        
        logData("Valid Email Partition", "✅ Business email format processed correctly");
    }
    
    @Test
    @Description("EP: Valid Email Format Partition - Personal email")
    public void testValidEmailPartition_PersonalEmail() {
        logStep("Testing Valid Email Partition: Personal email format");
        
        User user = new User()
                .username("personal_" + System.currentTimeMillis())
                .firstName("Personal")
                .lastName("User")
                .email("personal123@gmail.com") // Valid personal email partition
                .password("myPassword456")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getEmail()).isEqualTo("personal123@gmail.com");
        
        logData("Valid Email Partition", "✅ Personal email format processed correctly");
    }
    
    @Test
    @Description("EP: Username Format Partition - Alphanumeric characters")
    public void testUsernamePartition_Alphanumeric() {
        logStep("Testing Username Partition: Alphanumeric format");
        
        User user = new User()
                .username("user123abc") // Alphanumeric partition
                .firstName("Alpha")
                .lastName("Numeric")
                .email("alphanumeric@example.com")
                .password("password123")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getUsername()).isEqualTo("user123abc");
        
        logData("Alphanumeric Username", "✅ Mixed alphanumeric username accepted");
    }
    
    @Test
    @Description("EP: Username Format Partition - Special characters")
    public void testUsernamePartition_SpecialCharacters() {
        logStep("Testing Username Partition: Special characters");
        
        User user = new User()
                .username("user_123-test") // Special characters partition
                .firstName("Special")
                .lastName("Chars")
                .email("special@example.com")
                .password("password123")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getUsername()).isEqualTo("user_123-test");
        
        logData("Special Character Username", "✅ Username with underscores and hyphens accepted");
    }
    
    @Test
    @Description("EP: User Status Partition - Active user (status 1)")
    public void testUserStatusPartition_ActiveUser() {
        logStep("Testing User Status Partition: Active user");
        
        User user = new User()
                .username("active_" + System.currentTimeMillis())
                .firstName("Active")
                .lastName("User")
                .email("active@example.com")
                .password("password123")
                .userStatus(1); // Active user partition
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getUserStatus()).isEqualTo(1);
        
        logData("Active User Status", "✅ Active user status (1) processed correctly");
    }
    
    @Test
    @Description("EP: User Status Partition - Inactive user (status 0)")
    public void testUserStatusPartition_InactiveUser() {
        logStep("Testing User Status Partition: Inactive user");
        
        User user = new User()
                .username("inactive_" + System.currentTimeMillis())
                .firstName("Inactive")
                .lastName("User")
                .email("inactive@example.com")
                .password("password123")
                .userStatus(0); // Inactive user partition
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getUserStatus()).isEqualTo(0);
        
        logData("Inactive User Status", "✅ Inactive user status (0) processed correctly");
    }
    
    @Test
    @Description("EP: Password Partition - Simple passwords")
    public void testPasswordPartition_SimplePassword() {
        logStep("Testing Password Partition: Simple password format");
        
        User user = new User()
                .username("simple_" + System.currentTimeMillis())
                .firstName("Simple")
                .lastName("Password")
                .email("simple@example.com")
                .password("password") // Simple password partition
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getUsername()).contains("simple_");
        
        logData("Simple Password", "✅ Simple password format accepted");
    }
    
    @Test
    @Description("EP: Password Partition - Complex passwords")
    public void testPasswordPartition_ComplexPassword() {
        logStep("Testing Password Partition: Complex password format");
        
        User user = new User()
                .username("complex_" + System.currentTimeMillis())
                .firstName("Complex")
                .lastName("Password")
                .email("complex@example.com")
                .password("ComplexP@ssw0rd!123") // Complex password partition
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getUsername()).contains("complex_");
        
        logData("Complex Password", "✅ Complex password with special characters accepted");
    }
    
    @Test
    @Description("EP: Name Field Partition - Single word names")
    public void testNamePartition_SingleWord() {
        logStep("Testing Name Partition: Single word names");
        
        User user = new User()
                .username("single_" + System.currentTimeMillis())
                .firstName("John") // Single word partition
                .lastName("Doe") // Single word partition
                .email("single@example.com")
                .password("password123")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getFirstName()).isEqualTo("John");
        assertThat(retrievedUser.getLastName()).isEqualTo("Doe");
        
        logData("Single Word Names", "✅ Single word first and last names accepted");
    }
    
    @Test
    @Description("EP: Name Field Partition - Multi-word names with spaces")
    public void testNamePartition_MultiWord() {
        logStep("Testing Name Partition: Multi-word names");
        
        User user = new User()
                .username("multi_" + System.currentTimeMillis())
                .firstName("Mary Jane") // Multi-word partition
                .lastName("Van Der Berg") // Multi-word partition
                .email("multi@example.com")
                .password("password123")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getFirstName()).isEqualTo("Mary Jane");
        assertThat(retrievedUser.getLastName()).isEqualTo("Van Der Berg");
        
        logData("Multi Word Names", "✅ Multi-word names with spaces accepted");
    }
    
    @Override
    protected void cleanup() {
        for (String username : createdUsernames) {
            try {
                logStep("Cleaning up test user: " + username);
                userRestClient.deleteUser(username);
            } catch (Exception e) {
                logError("Cleanup failed for user: " + username, e);
            }
        }
        createdUsernames.clear();
    }
}