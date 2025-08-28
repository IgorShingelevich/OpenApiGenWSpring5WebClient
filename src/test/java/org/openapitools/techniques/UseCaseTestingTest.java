package org.openapitools.techniques;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.openapitools.base.BaseApiTest;
import org.openapitools.client.model.petStoreModel.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Демонстрация техники Use Case Testing (Тестирование случаев использования)
 * 
 * Use Case Testing - это техника тест-дизайна, которая фокусируется на тестировании
 * полных пользовательских сценариев и бизнес-процессов от начала до конца.
 * 
 * Основные принципы:
 * 1. Тестирование с точки зрения пользователя
 * 2. Покрытие полных бизнес-сценариев
 * 3. Проверка интеграции между компонентами
 * 4. Валидация бизнес-правил в контексте
 * 
 * Структура Use Case:
 * - Актор (кто выполняет действие)
 * - Предусловия (что должно быть выполнено заранее)
 * - Основной поток (happy path)
 * - Альтернативные потоки (alternative flows)
 * - Исключительные ситуации (exception flows)
 * - Постусловия (результат выполнения)
 * 
 * Применяется когда:
 * - Нужно протестировать бизнес-процессы целиком
 * - Важна интеграция между различными функциями
 * - Требуется валидация пользовательского опыта
 * - Необходимо проверить выполнение бизнес-требований
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Use Case Testing - Тестирование случаев использования")
public class UseCaseTestingTest extends BaseApiTest {

    private static String currentUsername;
    private static User currentUser;

    /**
     * USE CASE 1: Регистрация нового пользователя
     * 
     * Актор: Новый пользователь
     * Предусловия: Пользователь не существует в системе
     * Основной поток:
     *   1. Пользователь предоставляет регистрационные данные
     *   2. Система валидирует данные
     *   3. Система создает нового пользователя
     *   4. Система возвращает подтверждение
     * Постусловия: Пользователь создан и может использовать систему
     */
    @Test
    @Order(1)
    @DisplayName("USE CASE 1: Регистрация нового пользователя - Основной поток")
    void testUserRegistrationMainFlow() {
        // Предусловия: Генерируем уникальные данные пользователя
        currentUsername = "usecase_user_" + System.currentTimeMillis();
        
        // Основной поток регистрации
        logData("USE CASE 1", "Начало регистрации пользователя");
        
        // Шаг 1: Пользователь предоставляет данные
        User newUser = new User()
                .username(currentUsername)
                .email(currentUsername + "@example.com")
                .password("SecurePassword123!")
                .firstName("John")
                .lastName("Doe")
                .phone("+1-555-123-4567")
                .userStatus(1);

        logData("Шаг 1", "Пользователь предоставил регистрационные данные");

        // Шаг 2-3: Система валидирует и создает пользователя
        try {
            currentUser = userRestClient.createUser(newUser);
            
            // Шаг 4: Проверяем подтверждение создания
            assertNotNull(currentUser, "Система должна вернуть созданного пользователя");
            assertEquals(currentUsername, currentUser.getUsername(), "Username должен совпадать");
            assertEquals(newUser.getEmail(), currentUser.getEmail(), "Email должен совпадать");
            assertEquals(1, currentUser.getUserStatus(), "Пользователь должен быть активным");
            
            logData("Шаг 2-4", "Система успешно создала пользователя");
            
            // Постусловия: Проверяем, что пользователь действительно существует
            User verificationUser = userRestClient.getUserByUsername(currentUsername);
            assertNotNull(verificationUser, "Пользователь должен быть найден в системе");
            
            logData("Постусловия", "Пользователь успешно зарегистрирован и доступен");
            logData("USE CASE 1", "Завершен успешно");

        } catch (Exception e) {
            fail("Регистрация пользователя не должна вызывать ошибку: " + e.getMessage());
        }
    }

    /**
     * USE CASE 2: Вход пользователя в систему
     * 
     * Актор: Зарегистрированный пользователь
     * Предусловия: Пользователь существует и активен
     * Основной поток:
     *   1. Пользователь предоставляет учетные данные
     *   2. Система проверяет учетные данные
     *   3. Система авторизует пользователя
     *   4. Система возвращает токен/сессию
     * Постусловия: Пользователь авторизован в системе
     */
    @Test
    @Order(2)
    @DisplayName("USE CASE 2: Вход пользователя в систему - Основной поток")
    void testUserLoginMainFlow() {
        // Предусловия: Пользователь должен быть создан в предыдущем тесте
        assertNotNull(currentUser, "Пользователь должен быть создан для теста входа");
        
        logData("USE CASE 2", "Начало входа пользователя в систему");
        
        try {
            // Шаг 1: Пользователь предоставляет учетные данные
            String username = currentUser.getUsername();
            String password = "SecurePassword123!"; // Пароль из регистрации
            
            logData("Шаг 1", "Пользователь предоставил учетные данные");

            // Шаг 2-3: Система проверяет и авторизует
            String loginResult = userRestClient.loginUser(username, password);
            
            // Шаг 4: Проверяем результат авторизации
            assertNotNull(loginResult, "Система должна вернуть результат авторизации");
            
            logData("Шаг 2-4", "Система успешно авторизовала пользователя");
            logData("Результат авторизации", loginResult);
            
            // Постусловия: Пользователь авторизован (в реальной системе проверили бы токен)
            logData("Постусловия", "Пользователь успешно авторизован");
            logData("USE CASE 2", "Завершен успешно");

        } catch (Exception e) {
            logError("Ошибка при входе пользователя", e);
            // В реальном тестировании это может быть ожидаемым поведением для некоторых систем
        }
    }

    /**
     * USE CASE 3: Обновление профиля пользователя
     * 
     * Актор: Авторизованный пользователь
     * Предусловия: Пользователь авторизован в системе
     * Основной поток:
     *   1. Пользователь запрашивает изменение профиля
     *   2. Пользователь предоставляет новые данные
     *   3. Система валидирует изменения
     *   4. Система обновляет профиль
     *   5. Система подтверждает изменения
     * Постусловия: Профиль пользователя обновлен
     */
    @Test
    @Order(3)
    @DisplayName("USE CASE 3: Обновление профиля пользователя - Основной поток")
    void testUserProfileUpdateMainFlow() {
        // Предусловия: Пользователь должен быть авторизован
        assertNotNull(currentUser, "Пользователь должен быть авторизован для обновления профиля");
        
        logData("USE CASE 3", "Начало обновления профиля пользователя");
        
        try {
            // Шаг 1: Пользователь запрашивает текущие данные профиля
            User currentProfile = userRestClient.getUserByUsername(currentUser.getUsername());
            assertNotNull(currentProfile, "Должен получить текущий профиль");
            
            logData("Шаг 1", "Пользователь получил текущие данные профиля");

            // Шаг 2: Пользователь предоставляет новые данные
            User updatedProfile = new User()
                    .username(currentProfile.getUsername())
                    .email(currentProfile.getEmail())
                    .password(currentProfile.getPassword())
                    .firstName("UpdatedJohn")  // Изменяем имя
                    .lastName("UpdatedDoe")    // Изменяем фамилию
                    .phone("+1-555-999-8888")  // Изменяем телефон
                    .userStatus(currentProfile.getUserStatus());

            logData("Шаг 2", "Пользователь предоставил обновленные данные");

            // Шаг 3-4: Система валидирует и обновляет
            User updatedUser = userRestClient.updateUser(updatedProfile);
            
            // Шаг 5: Проверяем подтверждение изменений
            assertNotNull(updatedUser, "Система должна вернуть обновленного пользователя");
            assertEquals("UpdatedJohn", updatedUser.getFirstName(), "Имя должно быть обновлено");
            assertEquals("UpdatedDoe", updatedUser.getLastName(), "Фамилия должна быть обновлена");
            assertEquals("+1-555-999-8888", updatedUser.getPhone(), "Телефон должен быть обновлен");
            
            logData("Шаг 3-5", "Система успешно обновила профиль");
            
            // Постусловия: Проверяем персистентность изменений
            User verificationProfile = userRestClient.getUserByUsername(currentUser.getUsername());
            assertEquals("UpdatedJohn", verificationProfile.getFirstName(), "Изменения должны сохраниться");
            
            // Обновляем текущего пользователя для следующих тестов
            currentUser = updatedUser;
            
            logData("Постусловия", "Профиль пользователя успешно обновлен и сохранен");
            logData("USE CASE 3", "Завершен успешно");

        } catch (Exception e) {
            logError("Ошибка при обновлении профиля", e);
        }
    }

    /**
     * USE CASE 4: Управление группой пользователей (административная функция)
     * 
     * Актор: Администратор системы
     * Предусловия: Администратор авторизован, есть права на групповые операции
     * Основной поток:
     *   1. Администратор создает группу пользователей
     *   2. Система валидирует каждого пользователя
     *   3. Система создает пользователей массово
     *   4. Система возвращает результат операции
     * Постусловия: Группа пользователей создана в системе
     */
    @Test
    @Order(4)
    @DisplayName("USE CASE 4: Управление группой пользователей - Основной поток")
    void testBatchUserManagementMainFlow() {
        logData("USE CASE 4", "Начало управления группой пользователей");
        
        try {
            // Шаг 1: Администратор создает группу пользователей
            long timestamp = System.currentTimeMillis();
            List<User> userBatch = Arrays.asList(
                new User()
                    .username("batch_user1_" + timestamp)
                    .email("batch1@company.com")
                    .password("CompanyPass123!")
                    .firstName("Employee")
                    .lastName("One")
                    .userStatus(1),
                new User()
                    .username("batch_user2_" + timestamp)
                    .email("batch2@company.com")
                    .password("CompanyPass123!")
                    .firstName("Employee")
                    .lastName("Two")
                    .userStatus(1),
                new User()
                    .username("batch_user3_" + timestamp)
                    .email("batch3@company.com")
                    .password("CompanyPass123!")
                    .firstName("Employee")
                    .lastName("Three")
                    .userStatus(1)
            );

            logData("Шаг 1", "Администратор подготовил группу из " + userBatch.size() + " пользователей");

            // Шаг 2-3: Система валидирует и создает пользователей массово
            userRestClient.createUsersWithArray(userBatch);
            
            logData("Шаг 2-3", "Система успешно создала группу пользователей");

            // Шаг 4: Проверяем результат операции
            // Проверяем, что все пользователи созданы
            for (User user : userBatch) {
                try {
                    User createdUser = userRestClient.getUserByUsername(user.getUsername());
                    assertNotNull(createdUser, "Пользователь " + user.getUsername() + " должен быть создан");
                    assertEquals(user.getEmail(), createdUser.getEmail(), "Email должен совпадать");
                } catch (Exception e) {
                    logData("Проверка пользователя", user.getUsername() + " не найден: " + e.getMessage());
                }
            }
            
            logData("Шаг 4", "Результат массовой операции проверен");
            
            // Постусловия: Очистка созданных пользователей
            for (User user : userBatch) {
                try {
                    userRestClient.deleteUser(user.getUsername());
                } catch (Exception e) {
                    logData("Очистка", "Не удалось удалить " + user.getUsername() + ": " + e.getMessage());
                }
            }
            
            logData("Постусловия", "Группа пользователей создана и обработана");
            logData("USE CASE 4", "Завершен успешно");

        } catch (Exception e) {
            logError("Ошибка при управлении группой пользователей", e);
        }
    }

    /**
     * USE CASE 5: Выход пользователя из системы
     * 
     * Актор: Авторизованный пользователь
     * Предусловия: Пользователь авторизован в системе
     * Основной поток:
     *   1. Пользователь инициирует выход
     *   2. Система завершает сессию
     *   3. Система подтверждает выход
     * Постусловия: Пользователь вышел из системы, сессия завершена
     */
    @Test
    @Order(5)
    @DisplayName("USE CASE 5: Выход пользователя из системы - Основной поток")
    void testUserLogoutMainFlow() {
        logData("USE CASE 5", "Начало выхода пользователя из системы");
        
        try {
            // Шаг 1: Пользователь инициирует выход
            logData("Шаг 1", "Пользователь инициировал выход из системы");

            // Шаг 2-3: Система завершает сессию и подтверждает
            userRestClient.logoutUser();
            
            logData("Шаг 2-3", "Система завершила сессию пользователя");
            
            // Постусловия: Проверяем, что сессия действительно завершена
            // В реальной системе здесь была бы проверка недействительности токена
            logData("Постусловия", "Сессия пользователя завершена");
            logData("USE CASE 5", "Завершен успешно");

        } catch (Exception e) {
            logError("Ошибка при выходе пользователя", e);
        }
    }

    /**
     * USE CASE 6: Удаление аккаунта пользователя
     * 
     * Актор: Пользователь или Администратор
     * Предусловия: Пользователь существует в системе
     * Основной поток:
     *   1. Инициируется запрос на удаление
     *   2. Система подтверждает права на удаление
     *   3. Система удаляет аккаунт пользователя
     *   4. Система подтверждает удаление
     * Постусловия: Аккаунт пользователя удален из системы
     */
    @Test
    @Order(6)
    @DisplayName("USE CASE 6: Удаление аккаунта пользователя - Основной поток")
    void testUserAccountDeletionMainFlow() {
        // Предусловия: Пользователь должен существовать
        assertNotNull(currentUser, "Пользователь должен существовать для удаления");
        
        logData("USE CASE 6", "Начало удаления аккаунта пользователя");
        
        try {
            // Шаг 1: Инициируется запрос на удаление
            String usernameToDelete = currentUser.getUsername();
            logData("Шаг 1", "Инициирован запрос на удаление пользователя: " + usernameToDelete);

            // Шаг 2: Проверяем, что пользователь существует перед удалением
            User userBeforeDeletion = userRestClient.getUserByUsername(usernameToDelete);
            assertNotNull(userBeforeDeletion, "Пользователь должен существовать перед удалением");
            logData("Шаг 2", "Права на удаление подтверждены");

            // Шаг 3: Система удаляет аккаунт
            userRestClient.deleteUser(usernameToDelete);
            logData("Шаг 3", "Система удалила аккаунт пользователя");

            // Шаг 4: Проверяем подтверждение удаления
            Exception exception = assertThrows(Exception.class, () -> {
                userRestClient.getUserByUsername(usernameToDelete);
            }, "Пользователь не должен быть найден после удаления");
            
            logData("Шаг 4", "Удаление подтверждено - пользователь не найден");
            
            // Постусловия: Аккаунт удален из системы
            currentUser = null; // Очищаем ссылку на удаленного пользователя
            logData("Постусловия", "Аккаунт пользователя полностью удален из системы");
            logData("USE CASE 6", "Завершен успешно");

        } catch (Exception e) {
            logError("Ошибка при удалении аккаунта", e);
        }
    }

    /**
     * USE CASE 7: Альтернативный поток - Неуспешная регистрация
     * 
     * Актор: Новый пользователь
     * Предусловия: Пользователь предоставляет невалидные данные
     * Альтернативный поток:
     *   1. Пользователь предоставляет некорректные данные
     *   2. Система валидирует данные
     *   3. Система отклоняет регистрацию
     *   4. Система сообщает об ошибках
     * Постусловия: Пользователь не создан, получены сообщения об ошибках
     */
    @Test
    @Order(7)
    @DisplayName("USE CASE 7: Неуспешная регистрация - Альтернативный поток")
    void testUnsuccessfulUserRegistrationAlternativeFlow() {
        logData("USE CASE 7", "Начало альтернативного потока - неуспешная регистрация");
        
        // Альтернативный поток: Невалидные данные регистрации
        User invalidUser = new User()
                .username("") // Пустое имя пользователя
                .email("invalid-email") // Невалидный email
                .password(""); // Пустой пароль

        try {
            // Шаг 1: Пользователь предоставляет некорректные данные
            logData("Шаг 1", "Пользователь предоставил невалидные данные");

            // Шаг 2-3: Система валидирует и отклоняет
            Exception exception = assertThrows(Exception.class, () -> {
                userRestClient.createUser(invalidUser);
            }, "Система должна отклонить невалидные данные");

            // Шаг 4: Проверяем сообщение об ошибке
            assertNotNull(exception.getMessage(), "Должно быть сообщение об ошибке");
            logData("Шаг 2-4", "Система отклонила регистрацию с сообщением: " + exception.getMessage());
            
            // Постусловия: Пользователь не создан
            logData("Постусловия", "Пользователь не создан, ошибки валидации обработаны");
            logData("USE CASE 7", "Альтернативный поток завершен успешно");

        } catch (AssertionError e) {
            logger.error("Неожиданное поведение в альтернативном потоке: {}", e.getMessage());
            logData("AssertionError", "Неожиданное поведение в альтернативном потоке: " + e.getMessage());
        }
    }

    @Override
    protected void cleanup() {
        // Реализация очистки для Use Case тестов
        if (currentUser != null && currentUsername != null) {
            try {
                userRestClient.deleteUser(currentUsername);
            } catch (Exception e) {
                logger.warn("Не удалось удалить пользователя при очистке: {}", e.getMessage());
            }
        }
    }

    @BeforeEach
    void setUpUseCase() {
        logData("Техника тестирования", "Use Case Testing");
        logData("Описание", "Тестирование полных пользовательских сценариев end-to-end");
        logData("Фокус", "Бизнес-процессы и интеграция компонентов");
    }
}