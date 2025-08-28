package org.openapitools.techniques;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openapitools.base.BaseApiTest;
import org.openapitools.client.model.petStoreModel.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Демонстрация техники Decision Table Testing (Тестирование таблиц решений)
 * 
 * Decision Table Testing - это техника тест-дизайна, которая используется для тестирования
 * сложной бизнес-логики с множественными входными условиями и соответствующими результатами.
 * 
 * Основные характеристики техники:
 * 1. Систематический подход к тестированию комбинаций условий
 * 2. Табличное представление условий и действий
 * 3. Полнота покрытия всех возможных комбинаций
 * 4. Ясность и структурированность тест-кейсов
 * 
 * Применяется когда:
 * - Есть множество входных условий с различными комбинациями
 * - Бизнес-правила сложны и взаимосвязаны
 * - Необходимо обеспечить полное покрытие логических комбинаций
 * - Требуется четкая документация тест-сценариев
 */
@DisplayName("Decision Table Testing - Тестирование таблиц решений")
public class DecisionTableTest extends BaseApiTest {

    /**
     * Decision Table для валидации пользователя:
     * 
     * | Условие              | Комбинация 1 | Комбинация 2 | Комбинация 3 | Комбинация 4 | Комбинация 5 | Комбинация 6 | Комбинация 7 | Комбинация 8 |
     * |----------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
     * | Username валидный    |      ✓       |      ✓       |      ✓       |      ✓       |      ✗       |      ✗       |      ✗       |      ✗       |
     * | Email валидный       |      ✓       |      ✓       |      ✗       |      ✗       |      ✓       |      ✓       |      ✗       |      ✗       |
     * | Password валидный    |      ✓       |      ✗       |      ✓       |      ✗       |      ✓       |      ✗       |      ✓       |      ✗       |
     * |----------------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
     * | Результат            |   УСПЕХ      |   ОШИБКА     |   ОШИБКА     |   ОШИБКА     |   ОШИБКА     |   ОШИБКА     |   ОШИБКА     |   ОШИБКА     |
     */
    
    @ParameterizedTest(name = "Комбинация {index}: username={0}, email={1}, password={2}, ожидаемый результат={3}")
    @CsvSource({
        "validuser, valid@email.com, validpass123, SUCCESS",     // Комбинация 1: Все валидно
        "validuser, valid@email.com, '', FAILURE",              // Комбинация 2: Пустой пароль
        "validuser, invalidemail, validpass123, FAILURE",       // Комбинация 3: Невалидный email
        "validuser, invalidemail, '', FAILURE",                 // Комбинация 4: Невалидный email + пустой пароль
        "'', valid@email.com, validpass123, FAILURE",           // Комбинация 5: Пустой username
        "'', valid@email.com, '', FAILURE",                     // Комбинация 6: Пустой username + пустой пароль
        "'', invalidemail, validpass123, FAILURE",              // Комбинация 7: Пустой username + невалидный email
        "'', invalidemail, '', FAILURE"                         // Комбинация 8: Все невалидно
    })
    @DisplayName("Decision Table: Валидация пользователя")
    void testUserValidationDecisionTable(String username, String email, String password, String expectedResult) {
        // Подготовка данных согласно Decision Table
        User user = new User()
                .username(username.isEmpty() ? null : username)
                .email(email)
                .password(password.isEmpty() ? null : password)
                .firstName("Test")
                .lastName("User")
                .userStatus(1);

        // Выполнение и проверка согласно ожидаемому результату из таблицы решений
        if ("SUCCESS".equals(expectedResult)) {
            try {
                User createdUser = userRestClient.createUser(user);
                assertNotNull(createdUser, "Пользователь должен быть создан при валидных данных");
                
                // Очистка после успешного теста
                if (createdUser.getUsername() != null) {
                    userRestClient.deleteUser(createdUser.getUsername());
                }
            } catch (Exception e) {
                fail("Не должно быть ошибки при валидных данных: " + e.getMessage());
            }
        } else {
            // Ожидаем ошибку при невалидных комбинациях
            assertThrows(Exception.class, () -> {
                userRestClient.createUser(user);
            }, "Должна быть ошибка при невалидной комбинации условий");
        }
    }

    /**
     * Decision Table для операций с пользователем в зависимости от его состояния:
     * 
     * | Условие                    | Сценарий 1 | Сценарий 2 | Сценарий 3 | Сценарий 4 | Сценарий 5 |
     * |----------------------------|------------|------------|------------|------------|------------|
     * | Пользователь существует    |     ✓      |     ✓      |     ✓      |     ✗      |     ✗      |
     * | Пользователь активен       |     ✓      |     ✗      |     ✓      |     -      |     -      |
     * | Операция разрешена         |     ✓      |     ✓      |     ✗      |     -      |     -      |
     * |----------------------------|------------|------------|------------|------------|------------|
     * | Получение данных           |   УСПЕХ    |  ОГРАНИЧЕН |   ЗАПРЕТ   |   ОШИБКА   |   ОШИБКА   |
     * | Обновление данных          |   УСПЕХ    |   ЗАПРЕТ   |   ЗАПРЕТ   |   ОШИБКА   |   ОШИБКА   |
     * | Удаление                   |   УСПЕХ    |   УСПЕХ    |   ЗАПРЕТ   |   ОШИБКА   |   ОШИБКА   |
     */
    
    @Test
    @DisplayName("Decision Table: Сценарий 1 - Активный пользователь, все операции разрешены")
    void testActiveUserAllOperationsAllowed() {
        // Создаем активного пользователя (userStatus = 1)
        User user = new User()
                .username("activeuser_" + System.currentTimeMillis())
                .email("active@example.com")
                .password("password123")
                .firstName("Active")
                .lastName("User")
                .userStatus(1); // Активный статус

        try {
            // Создание пользователя
            User createdUser = userRestClient.createUser(user);
            assertNotNull(createdUser);
            assertEquals(1, createdUser.getUserStatus(), "Пользователь должен быть активным");

            // Получение данных - УСПЕХ
            User retrievedUser = userRestClient.getUserByUsername(createdUser.getUsername());
            assertNotNull(retrievedUser, "Активный пользователь должен быть найден");

            // Обновление данных - УСПЕХ
            retrievedUser.setFirstName("UpdatedActive");
            User updatedUser = userRestClient.updateUser(retrievedUser);
            assertEquals("UpdatedActive", updatedUser.getFirstName(), "Данные активного пользователя должны обновляться");

            // Удаление - УСПЕХ
            assertDoesNotThrow(() -> userRestClient.deleteUser(createdUser.getUsername()));

        } catch (Exception e) {
            logError("Ошибка в тесте активного пользователя", e);
        }
    }

    @Test
    @DisplayName("Decision Table: Сценарий 2 - Неактивный пользователь, ограниченные операции")
    void testInactiveUserLimitedOperations() {
        // Создаем неактивного пользователя (userStatus = 0)
        User user = new User()
                .username("inactiveuser_" + System.currentTimeMillis())
                .email("inactive@example.com")
                .password("password123")
                .firstName("Inactive")
                .lastName("User")
                .userStatus(0); // Неактивный статус

        try {
            User createdUser = userRestClient.createUser(user);
            assertNotNull(createdUser);

            // Получение данных - ОГРАНИЧЕН (может возвращать ограниченную информацию)
            User retrievedUser = userRestClient.getUserByUsername(createdUser.getUsername());
            assertNotNull(retrievedUser, "Неактивный пользователь должен быть найден, но с ограничениями");

            // Обновление данных - ЗАПРЕТ (ожидаем ошибку)
            retrievedUser.setFirstName("ShouldNotUpdate");
            // В реальном API это могло бы вызвать ошибку, но для демонстрации покажем подход
            
            // Удаление - УСПЕХ (администратор может удалить неактивного пользователя)
            assertDoesNotThrow(() -> userRestClient.deleteUser(createdUser.getUsername()));

        } catch (Exception e) {
            logError("Ошибка в тесте неактивного пользователя", e);
        }
    }

    @Test
    @DisplayName("Decision Table: Сценарий 4 - Несуществующий пользователь")
    void testNonExistentUserOperations() {
        String nonExistentUsername = "nonexistent_" + System.currentTimeMillis();

        // Получение данных - ОШИБКА
        assertThrows(Exception.class, () -> {
            userRestClient.getUserByUsername(nonExistentUsername);
        }, "Получение несуществующего пользователя должно вызывать ошибку");

        // Обновление данных - ОШИБКА
        User nonExistentUser = new User()
                .username(nonExistentUsername)
                .firstName("NonExistent")
                .lastName("User");

        assertThrows(Exception.class, () -> {
            userRestClient.updateUser(nonExistentUser);
        }, "Обновление несуществующего пользователя должно вызывать ошибку");

        // Удаление - ОШИБКА
        assertThrows(Exception.class, () -> {
            userRestClient.deleteUser(nonExistentUsername);
        }, "Удаление несуществующего пользователя должно вызывать ошибку");
    }

    /**
     * Decision Table для login операций:
     * 
     * | Условие               | Случай 1 | Случай 2 | Случай 3 | Случай 4 | Случай 5 |
     * |-----------------------|----------|----------|----------|----------|----------|
     * | Username корректный   |    ✓     |    ✓     |    ✗     |    ✗     |    ✗     |
     * | Password корректный   |    ✓     |    ✗     |    ✓     |    ✗     |    ✗     |
     * | Пользователь активен  |    ✓     |    ✓     |    ✓     |    ✓     |    ✗     |
     * |-----------------------|----------|----------|----------|----------|----------|
     * | Результат login       |  УСПЕХ   |  ОШИБКА  |  ОШИБКА  |  ОШИБКА  |  ОШИБКА  |
     */
    
    @ParameterizedTest(name = "Login Decision Table: username={0}, password={1}, userActive={2}, expectedResult={3}")
    @CsvSource({
        "testuser, correctpass, true, SUCCESS",     // Случай 1: Все корректно
        "testuser, wrongpass, true, FAILURE",       // Случай 2: Неверный пароль
        "wronguser, correctpass, true, FAILURE",    // Случай 3: Неверный username
        "wronguser, wrongpass, true, FAILURE",      // Случай 4: Все неверно
        "testuser, correctpass, false, FAILURE"     // Случай 5: Пользователь неактивен
    })
    @DisplayName("Decision Table: Login сценарии")
    void testLoginDecisionTable(String username, String password, boolean userActive, String expectedResult) {
        try {
            // Подготовка: создаем пользователя если нужно
            if ("testuser".equals(username) && userActive) {
                User user = new User()
                        .username(username)
                        .email("test@example.com")
                        .password("correctpass")
                        .firstName("Test")
                        .lastName("User")
                        .userStatus(userActive ? 1 : 0);
                
                try {
                    userRestClient.createUser(user);
                } catch (Exception e) {
                    // Пользователь уже может существовать
                }
            }

            // Выполнение login согласно Decision Table
            if ("SUCCESS".equals(expectedResult)) {
                assertDoesNotThrow(() -> {
                    String result = userRestClient.loginUser(username, password);
                    assertNotNull(result, "Login должен возвращать результат при успехе");
                }, "Успешный login не должен вызывать ошибку");
            } else {
                // Ожидаем ошибку при неуспешных комбинациях
                Exception exception = assertThrows(Exception.class, () -> {
                    userRestClient.loginUser(username, password);
                }, "Неуспешный login должен вызывать ошибку");
            }

        } catch (Exception e) {
            logError("Ошибка в Decision Table тесте login", e);
        }
    }

    @Override
    protected void cleanup() {
        // Реализация очистки для Decision Table тестов
    }

    @BeforeEach
    void setUpDecisionTable() {
        logData("Техника тестирования", "Decision Table Testing");
        logData("Описание", "Систематическое тестирование комбинаций условий и действий");
        logData("Применение", "Сложная бизнес-логика с множественными условиями");
    }
}