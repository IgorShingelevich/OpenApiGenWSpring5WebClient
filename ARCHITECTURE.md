# Архитектура тестового фреймворка для API слоя

## Обзор

Данный фреймворк построен на основе OpenAPI Generator и Spring WebClient для тестирования REST API. Архитектура следует принципам SOLID и слоистой архитектуры.

## Структура проекта

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
        └── pets/
            └── PetApiTest.java                # Тесты для Pet API
```

## Архитектурные принципы

### 1. Слоистая архитектура (Layered Architecture)

- **Инфраструктурный слой**: `RestClient` - базовый HTTP клиент
- **Сервисный слой**: `PetRestClient`, `UserRestClient` - специфичные клиенты
- **Презентационный слой**: Тестовые классы - бизнес-логика тестов

### 2. Принципы SOLID

#### Single Responsibility Principle (SRP)
- `RestClient` отвечает только за HTTP операции
- `PetRestClient` отвечает только за операции с питомцами
- `TestDataProvider` отвечает только за генерацию тестовых данных

#### Open/Closed Principle (OCP)
- Базовые классы открыты для расширения, закрыты для модификации
- Новые API клиенты могут быть добавлены без изменения существующих

#### Dependency Inversion Principle (DIP)
- Зависимости от абстракций (`BaseRestClient`)
- Внедрение зависимостей через конструктор

### 3. Паттерны проектирования

#### Composition over Inheritance
```java
public class PetRestClient extends BaseRestClient {
    private final RestClient restClient; // Композиция
    
    public PetRestClient(RestClient restClient) {
        super(restClient);
        this.restClient = restClient;
    }
}
```

#### Template Method Pattern
```java
public abstract class BaseApiTest {
    protected abstract void cleanup(); // Шаблонный метод
}
```

#### Factory Pattern (TestDataProvider)
```java
public class TestDataProvider {
    public static Pet createTestPet() { ... }
    public static User createTestUser() { ... }
}
```

## Стандарты оформления

### 1. Именование

#### Классы
- `{Entity}RestClient` - клиенты для конкретных сущностей
- `{Entity}ApiTest` - тестовые классы
- `Base{Type}` - базовые абстрактные классы

#### Методы
- `{action}{Entity}` - методы клиентов (createPet, getPetById)
- `test{Action}{Entity}` - тестовые методы (testCreatePet)
- `create{Entity}` - методы создания тестовых данных

#### Константы
- `{PURPOSE}_{DESCRIPTION}` - ENDPOINT_NAME, TIMEOUT_VALUE

### 2. Структура тестов

#### AAA Pattern (Arrange-Act-Assert)
```java
@Test
public void testCreatePet() {
    // Arrange
    Pet testPet = TestDataProvider.createTestPet();
    
    // Act
    Pet createdPet = petRestClient.createPet(testPet);
    
    // Assert
    assertThat(createdPet.getId()).isNotNull();
}
```

#### Логирование и отчетность
```java
logStep("Создание нового питомца");
logData("Test Pet", testPet);
logError("Cleanup failed", e);
```

### 3. Обработка ошибок

#### Graceful Degradation
```java
try {
    petRestClient.deletePet(petId);
} catch (Exception e) {
    logError("Cleanup failed", e);
    // Тест не падает из-за ошибки очистки
}
```

#### Cleanup Pattern
```java
@AfterEach
void tearDown() {
    cleanup(); // Гарантированная очистка тестовых данных
}
```

## Связи между компонентами

### 1. RestClient → PetRestClient/UserRestClient
- **Тип связи**: Композиция
- **Направление**: PetRestClient использует RestClient
- **Принцип**: Dependency Injection через конструктор

### 2. BaseRestClient → PetRestClient/UserRestClient
- **Тип связи**: Наследование
- **Направление**: PetRestClient наследует BaseRestClient
- **Принцип**: Переиспользование общей функциональности

### 3. BaseApiTest → PetApiTest
- **Тип связи**: Наследование
- **Направление**: PetApiTest наследует BaseApiTest
- **Принцип**: Переиспользование тестовой инфраструктуры

### 4. TestDataProvider → PetApiTest
- **Тип связи**: Использование
- **Направление**: PetApiTest использует TestDataProvider
- **Принцип**: Разделение ответственности

## Преимущества архитектуры

### 1. Масштабируемость
- Легко добавлять новые API клиенты
- Переиспользование кода через базовые классы
- Модульная структура

### 2. Поддерживаемость
- Четкое разделение ответственности
- Единообразное именование
- Документированный код

### 3. Тестируемость
- Изолированные компоненты
- Mock-объекты для тестирования
- Четкие интерфейсы

### 4. Расширяемость
- Новые типы тестовых данных
- Дополнительные HTTP методы
- Кастомные конфигурации

## Рекомендации по использованию

### 1. Создание нового API клиента
1. Создать класс `{Entity}RestClient extends BaseRestClient`
2. Реализовать CRUD операции
3. Добавить специфичные методы API

### 2. Создание новых тестов
1. Создать класс `{Entity}ApiTest extends BaseApiTest`
2. Реализовать методы тестирования
3. Добавить cleanup логику

### 3. Добавление тестовых данных
1. Расширить `TestDataProvider`
2. Создать методы для новой сущности
3. Использовать в тестах

### 4. Конфигурация
1. Настроить `WebClientConfig` для нового окружения
2. Добавить переменные окружения
3. Обновить таймауты при необходимости
