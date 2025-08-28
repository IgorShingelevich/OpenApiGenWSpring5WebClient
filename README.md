# Тестовый фреймворк для API слоя на основе Swagger

## Обзор

Данный проект представляет собой полноценный тестовый фреймворк для API слоя, построенный на основе OpenAPI Generator и Spring WebClient. Архитектура следует принципам SOLID и слоистой архитектуры.

## Архитектура

### Структура проекта

```
src/
├── main/java/
│   └── org/openapitools/client/
│       ├── RestClient.java                    # Базовый HTTP клиент
│       ├── clients/
│       │   ├── BaseRestClient.java            # Абстрактный базовый класс
│       │   ├── PetRestClient.java             # Клиент для Pet API
│       │   └── UserRestClient.java            # Клиент для User API
│       └── config/
│           └── WebClientConfig.java           # Конфигурация WebClient
└── test/java/
    └── org/openapitools/
        ├── base/
        │   ├── BaseApiTest.java               # Базовый тестовый класс
        │   └── TestDataProvider.java          # Провайдер тестовых данных
        ├── pets/
        │   └── PetApiTest.java                # Тесты для Pet API
        └── users/
            └── UserApiTest.java               # Тесты для User API
```

### Принципы архитектуры

#### 1. Слоистая архитектура (Layered Architecture)
- **Инфраструктурный слой**: `RestClient` - базовый HTTP клиент с WebClient
- **Сервисный слой**: `PetRestClient`, `UserRestClient` - специфичные клиенты для каждого API
- **Презентационный слой**: Тестовые классы с бизнес-логикой тестов

#### 2. Принципы SOLID
- **SRP (Single Responsibility Principle)**: Каждый класс отвечает за одну область
- **OCP (Open/Closed Principle)**: Базовые классы открыты для расширения, закрыты для модификации
- **DIP (Dependency Inversion Principle)**: Зависимости от абстракций через конструктор

#### 3. Паттерны проектирования
- **Composition over Inheritance**: Предпочтение композиции над наследованием
- **Template Method**: Базовые классы с абстрактными методами
- **Factory Pattern**: Провайдер тестовых данных

## Запуск тестов

### Предварительные требования
- Java 11+
- Maven 3.6+

### Команды

```bash
# Очистка и запуск всех тестов
mvn clean test

# Запуск только тестов Pet API
mvn test -Dtest=PetApiTest

# Запуск только тестов User API
mvn test -Dtest=UserApiTest

# Запуск с подробным логированием
mvn test -Dlogging.level.org.openapitools=DEBUG
```

## Особенности реализации

### 1. Обработка ошибок
Фреймворк корректно обрабатывает ошибки API и предоставляет детальное логирование:
- Автоматическое логирование всех HTTP запросов
- Обработка 404 ошибок для демо API
- Graceful handling исключений

### 2. Тестовые данные
- Автоматическая генерация уникальных тестовых данных
- Поддержка полных и минимальных объектов
- Автоматическая очистка после тестов

### 3. Логирование
- Структурированное логирование с использованием SLF4J
- Интеграция с Allure для отчетов
- Детальная информация о каждом шаге теста

## Расширение фреймворка

### Добавление нового API клиента

1. Создайте новый класс, наследующий от `BaseRestClient`:
```java
@Component
public class NewApiRestClient extends BaseRestClient {
    public NewApiRestClient(RestClient restClient) {
        super(restClient);
    }
    
    // Реализуйте методы для вашего API
}
```

2. Создайте тестовый класс, наследующий от `BaseApiTest`:
```java
public class NewApiTest extends BaseApiTest {
    @Test
    public void testYourApi() {
        // Ваши тесты
    }
}
```

### Добавление новых тестовых данных

Расширьте `TestDataProvider`:
```java
public static YourModel createTestYourModel() {
    return new YourModel()
        .field1("value1")
        .field2("value2");
}
```

## Преимущества архитектуры

1. **Масштабируемость**: Легко добавлять новые API и тесты
2. **Переиспользование**: Общая функциональность в базовых классах
3. **Читаемость**: Четкое разделение ответственности
4. **Поддерживаемость**: Изменения в одном месте влияют на все тесты
5. **Надежность**: Автоматическая очистка и обработка ошибок

## Технологии

- **Java 11+**
- **Spring WebClient** - для HTTP запросов
- **JUnit 5** - для тестирования
- **AssertJ** - для assertions
- **SLF4J + Logback** - для логирования
- **Allure** - для отчетов
- **Maven** - для сборки

## Лицензия

MIT License
