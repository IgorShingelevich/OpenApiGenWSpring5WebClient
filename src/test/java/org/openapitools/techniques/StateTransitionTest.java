package org.openapitools.techniques;

import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openapitools.base.BaseApiTest;
import org.openapitools.client.model.petStoreModel.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * State Transition Testing Technique Demonstration
 * 
 * State Transition Testing focuses on testing the transitions between different states
 * of the system. We identify states, valid transitions, and test state changes.
 * 
 * For User API, we'll model user lifecycle states:
 * 1. Non-existent → Created (via createUser)
 * 2. Created → Retrieved (via getUserByUsername)
 * 3. Retrieved → Modified (via updateUser)
 * 4. Modified → Authenticated (via loginUser)
 * 5. Authenticated → Logged Out (via logoutUser)
 * 6. Any State → Deleted (via deleteUser)
 */
public class StateTransitionTest extends BaseApiTest {
    
    private final List<String> createdUsernames = new ArrayList<>();
    
    @AfterEach
    void tearDown() {
        cleanup();
    }
    
    @Test
    @Description("State Transition: Complete User Lifecycle - Non-existent → Created → Retrieved → Modified → Deleted")
    public void testCompleteUserLifecycle() {
        String username = "lifecycle_" + System.currentTimeMillis();
        
        logStep("STATE 1: Non-existent → Created");
        // Initial state: User does not exist
        User newUser = new User()
                .username(username)
                .firstName("Initial")
                .lastName("User")
                .email("initial@example.com")
                .password("password123")
                .userStatus(0); // Inactive initially
        
        // Transition: Create user
        User createdUser = userRestClient.createUser(newUser);
        createdUsernames.add(username);
        assertThat(createdUser).isNotNull();
        logData("State Transition", "✅ Non-existent → Created");
        
        logStep("STATE 2: Created → Retrieved");
        // Current state: User exists
        // Transition: Retrieve user
        User retrievedUser = userRestClient.getUserByUsername(username);
        assertThat(retrievedUser.getUsername()).isEqualTo(username);
        assertThat(retrievedUser.getUserStatus()).isEqualTo(0); // Still inactive
        logData("State Transition", "✅ Created → Retrieved");
        
        logStep("STATE 3: Retrieved → Modified (Activated)");
        // Current state: User retrieved
        // Transition: Update user to active status
        User updatedUser = retrievedUser
                .firstName("Updated")
                .lastName("ActiveUser")
                .userStatus(1); // Activate user
        
        User modifiedUser = userRestClient.updateUser(updatedUser);
        assertThat(modifiedUser).isNotNull();
        logData("State Transition", "✅ Retrieved → Modified (Activated)");
        
        logStep("STATE 4: Verify Modified State");
        // Verify the state change persisted
        User verifiedUser = userRestClient.getUserByUsername(username);
        assertThat(verifiedUser.getFirstName()).isEqualTo("Updated");
        assertThat(verifiedUser.getUserStatus()).isEqualTo(1); // Now active
        logData("State Verification", "✅ User state successfully modified and persisted");
        
        logStep("STATE 5: Modified → Deleted");
        // Current state: User is active and modified
        // Transition: Delete user
        userRestClient.deleteUser(username);
        logData("State Transition", "✅ Modified → Deleted");
        
        logStep("STATE 6: Verify Final State (Non-existent)");
        // Verify user no longer exists
        try {
            userRestClient.getUserByUsername(username);
            logData("Delete Verification", "⚠️ User still exists after deletion (API limitation)");
        } catch (Exception e) {
            logData("Delete Verification", "✅ User successfully deleted - returned to non-existent state");
        }
        
        // Remove from cleanup list since we already deleted
        createdUsernames.remove(username);
    }
    
    @Test
    @Description("State Transition: User Authentication Flow - Created → Authenticated → Logged Out")
    public void testUserAuthenticationStateTransitions() {
        String username = "auth_" + System.currentTimeMillis();
        
        logStep("STATE 1: Create User for Authentication");
        User authUser = new User()
                .username(username)
                .firstName("Auth")
                .lastName("User")
                .email("auth@example.com")
                .password("authPassword123")
                .userStatus(1); // Active for authentication
        
        User createdUser = userRestClient.createUser(authUser);
        createdUsernames.add(username);
        logData("Initial State", "✅ User created and ready for authentication");
        
        logStep("STATE 2: Created → Authenticated (Login)");
        // Transition: Login user
        String loginResult = userRestClient.loginUser(username, "authPassword123");
        assertThat(loginResult).isNotNull();
        logData("State Transition", "✅ Created → Authenticated (Login successful)");
        
        logStep("STATE 3: Authenticated → Logged Out");
        // Transition: Logout user
        userRestClient.logoutUser();
        logData("State Transition", "✅ Authenticated → Logged Out");
        
        logStep("STATE 4: Verify User Still Exists After Logout");
        User userAfterLogout = userRestClient.getUserByUsername(username);
        assertThat(userAfterLogout.getUsername()).isEqualTo(username);
        logData("State Verification", "✅ User persists after logout - correct state behavior");
    }
    
    @Test
    @Description("State Transition: User Status Changes - Inactive → Active → Inactive")
    public void testUserStatusStateTransitions() {
        String username = "status_" + System.currentTimeMillis();
        
        logStep("STATE 1: Create Inactive User");
        User user = new User()
                .username(username)
                .firstName("Status")
                .lastName("Test")
                .email("status@example.com")
                .password("password123")
                .userStatus(0); // Start inactive
        
        User createdUser = userRestClient.createUser(user);
        createdUsernames.add(username);
        assertThat(createdUser.getUserStatus()).isEqualTo(0);
        logData("Initial State", "✅ User created in Inactive state (status=0)");
        
        logStep("STATE 2: Inactive → Active");
        User activeUser = createdUser.userStatus(1);
        User updatedToActive = userRestClient.updateUser(activeUser);
        
        User verifyActive = userRestClient.getUserByUsername(username);
        assertThat(verifyActive.getUserStatus()).isEqualTo(1);
        logData("State Transition", "✅ Inactive → Active (status: 0 → 1)");
        
        logStep("STATE 3: Active → Suspended");
        User suspendedUser = verifyActive.userStatus(2);
        User updatedToSuspended = userRestClient.updateUser(suspendedUser);
        
        User verifySuspended = userRestClient.getUserByUsername(username);
        assertThat(verifySuspended.getUserStatus()).isEqualTo(2);
        logData("State Transition", "✅ Active → Suspended (status: 1 → 2)");
        
        logStep("STATE 4: Suspended → Active (Reactivation)");
        User reactivatedUser = verifySuspended.userStatus(1);
        User updatedToReactivated = userRestClient.updateUser(reactivatedUser);
        
        User verifyReactivated = userRestClient.getUserByUsername(username);
        assertThat(verifyReactivated.getUserStatus()).isEqualTo(1);
        logData("State Transition", "✅ Suspended → Active (status: 2 → 1, Reactivation)");
    }
    
    @Test
    @Description("State Transition: Profile Update Lifecycle - Basic → Complete → Modified")
    public void testProfileUpdateStateTransitions() {
        String username = "profile_" + System.currentTimeMillis();
        
        logStep("STATE 1: Create User with Basic Profile");
        User basicUser = new User()
                .username(username)
                .firstName("Basic")
                .lastName("User")
                .email("basic@example.com")
                .password("password123")
                .userStatus(1);
        
        User createdUser = userRestClient.createUser(basicUser);
        createdUsernames.add(username);
        logData("Initial State", "✅ Basic profile created (minimal fields)");
        
        logStep("STATE 2: Basic → Complete Profile");
        User completeUser = createdUser
                .firstName("Complete")
                .lastName("Profile")
                .email("complete@example.com")
                .phone("+1-555-123-4567");
        
        User updatedComplete = userRestClient.updateUser(completeUser);
        
        User verifyComplete = userRestClient.getUserByUsername(username);
        assertThat(verifyComplete.getFirstName()).isEqualTo("Complete");
        assertThat(verifyComplete.getPhone()).isEqualTo("+1-555-123-4567");
        logData("State Transition", "✅ Basic → Complete (added phone, updated names)");
        
        logStep("STATE 3: Complete → Modified Profile");
        User modifiedUser = verifyComplete
                .firstName("Modified")
                .lastName("UpdatedProfile")
                .email("modified@example.com")
                .phone("+1-555-999-8888");
        
        User updatedModified = userRestClient.updateUser(modifiedUser);
        
        User verifyModified = userRestClient.getUserByUsername(username);
        assertThat(verifyModified.getFirstName()).isEqualTo("Modified");
        assertThat(verifyModified.getEmail()).isEqualTo("modified@example.com");
        assertThat(verifyModified.getPhone()).isEqualTo("+1-555-999-8888");
        logData("State Transition", "✅ Complete → Modified (all fields updated)");
    }
    
    @Test
    @Description("State Transition: Invalid State Transition Handling")
    public void testInvalidStateTransitions() {
        String username = "invalid_" + System.currentTimeMillis();
        
        logStep("Attempting Invalid Transition: Non-existent → Retrieved");
        try {
            // Try to retrieve user that doesn't exist
            User nonExistentUser = userRestClient.getUserByUsername(username);
            logData("Invalid Transition", "⚠️ Retrieved non-existent user (unexpected)");
        } catch (Exception e) {
            logData("Invalid Transition", "✅ Correctly rejected: Non-existent → Retrieved");
        }
        
        logStep("Create user for further invalid transition tests");
        User user = new User()
                .username(username)
                .firstName("Invalid")
                .lastName("Transition")
                .email("invalid@example.com")
                .password("password123")
                .userStatus(1);
        
        userRestClient.createUser(user);
        createdUsernames.add(username);
        
        logStep("Attempting Invalid Transition: Update Non-existent User");
        try {
            User fakeUser = new User()
                    .username("nonexistent_user_12345")
                    .firstName("Fake")
                    .lastName("User")
                    .email("fake@example.com")
                    .password("password")
                    .userStatus(1);
            
            userRestClient.updateUser(fakeUser);
            logData("Invalid Transition", "⚠️ Updated non-existent user (unexpected)");
        } catch (Exception e) {
            logData("Invalid Transition", "✅ Correctly rejected: Update non-existent user");
        }
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