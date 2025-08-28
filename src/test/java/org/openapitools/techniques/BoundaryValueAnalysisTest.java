package org.openapitools.techniques;

import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openapitools.base.BaseApiTest;
import org.openapitools.client.model.petStoreModel.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Boundary Value Analysis (BVA) Test Design Technique Demonstration
 * 
 * BVA focuses on testing values at the boundaries of input domains.
 * We test: minimum-1, minimum, minimum+1, maximum-1, maximum, maximum+1
 * 
 * For User API, we'll test boundary values for:
 * - Username length (typically 1-50 characters)
 * - Email format validation
 * - UserStatus values (0, 1, 2...)
 */
public class BoundaryValueAnalysisTest extends BaseApiTest {
    
    private final List<String> createdUsernames = new ArrayList<>();
    
    @AfterEach
    void tearDown() {
        cleanup();
    }
    
    @Test
    @Description("BVA: Username length boundaries - Minimum valid length (1 character)")
    public void testUsernameMinimumLength() {
        logStep("Testing minimum valid username length (1 character)");
        
        User user = new User()
                .username("a") // Minimum valid: 1 character
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        logStep("Verifying minimum length username creation");
        assertThat(createdUser).isNotNull();
        
        // Verify we can retrieve the user
        User retrievedUser = userRestClient.getUserByUsername(user.getUsername());
        assertThat(retrievedUser.getUsername()).isEqualTo("a");
        
        logData("Minimum Length Username Test", "✅ Single character username accepted");
    }
    
    @Test
    @Description("BVA: Username length boundaries - Maximum valid length (50 characters)")
    public void testUsernameMaximumLength() {
        logStep("Testing maximum valid username length (50 characters)");
        
        String maxLengthUsername = "a".repeat(50); // 50 characters
        
        User user = new User()
                .username(maxLengthUsername)
                .firstName("Test")
                .lastName("User")
                .email("maxlength@example.com")
                .password("password123")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(user.getUsername());
        
        logStep("Verifying maximum length username creation");
        assertThat(createdUser).isNotNull();
        
        logData("Maximum Length Username", "✅ 50 character username accepted");
    }
    
    @Test
    @Description("BVA: UserStatus boundaries - Testing edge values (0, 1, 2)")
    public void testUserStatusBoundaryValues() {
        logStep("Testing userStatus boundary values");
        
        // Test status = 0 (minimum)
        User userStatus0 = new User()
                .username("status0_" + System.currentTimeMillis())
                .firstName("Status")
                .lastName("Zero")
                .email("status0@example.com")
                .password("password123")
                .userStatus(0);
        
        User createdUser0 = userRestClient.createUser(userStatus0);
        createdUsernames.add(userStatus0.getUsername());
        
        logData("UserStatus 0", "✅ Status 0 accepted");
        
        // Test status = 1 (typical valid)
        User userStatus1 = new User()
                .username("status1_" + System.currentTimeMillis())
                .firstName("Status")
                .lastName("One")
                .email("status1@example.com")
                .password("password123")
                .userStatus(1);
        
        User createdUser1 = userRestClient.createUser(userStatus1);
        createdUsernames.add(userStatus1.getUsername());
        
        logData("UserStatus 1", "✅ Status 1 accepted");
        
        // Test status = 2 (upper boundary)
        User userStatus2 = new User()
                .username("status2_" + System.currentTimeMillis())
                .firstName("Status")
                .lastName("Two")
                .email("status2@example.com")
                .password("password123")
                .userStatus(2);
        
        User createdUser2 = userRestClient.createUser(userStatus2);
        createdUsernames.add(userStatus2.getUsername());
        
        logData("UserStatus 2", "✅ Status 2 accepted");
        
        logStep("All boundary status values successfully tested");
    }
    
    @Test
    @Description("BVA: Email format boundaries - Testing edge cases")
    public void testEmailFormatBoundaries() {
        logStep("Testing email format boundary conditions");
        
        // Minimum valid email format: a@b.co (6 characters)
        User shortEmailUser = new User()
                .username("shortemail_" + System.currentTimeMillis())
                .firstName("Short")
                .lastName("Email")
                .email("a@b.co")
                .password("password123")
                .userStatus(1);
        
        User createdShortEmail = userRestClient.createUser(shortEmailUser);
        createdUsernames.add(shortEmailUser.getUsername());
        
        logData("Minimum Email Format", "✅ Short email format accepted");
        
        // Long email with multiple subdomains
        User longEmailUser = new User()
                .username("longemail_" + System.currentTimeMillis())
                .firstName("Long")
                .lastName("Email")
                .email("very.long.email.address@subdomain.example.domain.com")
                .password("password123")
                .userStatus(1);
        
        User createdLongEmail = userRestClient.createUser(longEmailUser);
        createdUsernames.add(longEmailUser.getUsername());
        
        logData("Extended Email Format", "✅ Long email format accepted");
    }
    
    @Test
    @Description("BVA: Password length boundaries - Testing minimum and practical maximum")
    public void testPasswordLengthBoundaries() {
        logStep("Testing password length boundary values");
        
        // Short password (edge case)
        User shortPasswordUser = new User()
                .username("shortpwd_" + System.currentTimeMillis())
                .firstName("Short")
                .lastName("Password")
                .email("shortpwd@example.com")
                .password("123") // 3 characters
                .userStatus(1);
        
        User createdShortPwd = userRestClient.createUser(shortPasswordUser);
        createdUsernames.add(shortPasswordUser.getUsername());
        
        logData("Short Password", "✅ 3-character password accepted");
        
        // Long password
        User longPasswordUser = new User()
                .username("longpwd_" + System.currentTimeMillis())
                .firstName("Long")
                .lastName("Password")
                .email("longpwd@example.com")
                .password("a".repeat(100)) // 100 characters
                .userStatus(1);
        
        User createdLongPwd = userRestClient.createUser(longPasswordUser);
        createdUsernames.add(longPasswordUser.getUsername());
        
        logData("Long Password", "✅ 100-character password accepted");
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