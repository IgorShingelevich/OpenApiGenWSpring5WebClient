package org.openapitools.client.clients;

import org.openapitools.client.RestClient;
import org.openapitools.client.model.petStoreModel.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * REST клиент для работы с User API.
 * Предоставляет типизированные методы для CRUD операций с пользователями.
 */
@Component
public class UserRestClient extends BaseRestClient {
    
    private static final String USER_ENDPOINT = "user";
    private static final String USER_LOGIN_ENDPOINT = "user/login";
    private static final String USER_LOGOUT_ENDPOINT = "user/logout";
    private static final String USER_CREATE_WITH_ARRAY_ENDPOINT = "user/createWithArray";
    private static final String USER_CREATE_WITH_LIST_ENDPOINT = "user/createWithList";
    
    public UserRestClient(RestClient restClient) {
        super(restClient);
    }
    
    /**
     * Создать нового пользователя
     */
    public User createUser(User user) {
        return restClient.post(user, User.class, USER_ENDPOINT);
    }
    
    /**
     * Получить пользователя по username
     */
    public User getUserByUsername(String username) {
        return restClient.get(User.class, USER_ENDPOINT, username);
    }
    
    /**
     * Обновить пользователя
     */
    public User updateUser(User user) {
        return restClient.put(user, User.class, USER_ENDPOINT, user.getUsername());
    }
    
    /**
     * Удалить пользователя по username
     */
    public void deleteUser(String username) {
        restClient.delete(Void.class, USER_ENDPOINT, username);
    }
    
    /**
     * Вход пользователя в систему
     */
    public String loginUser(String username, String password) {
        List<String> queryParams = java.util.Arrays.asList(
            "username=" + username,
            "password=" + password
        );
        return restClient.get(String.class, queryParams, "user", "login");
    }
    
    /**
     * Выход пользователя из системы
     */
    public void logoutUser() {
        restClient.get(Void.class, "user", "logout");
    }
    
    /**
     * Создать пользователей из массива
     */
    public void createUsersWithArray(List<User> users) {
        restClient.post(users, Void.class, "user", "createWithArray");
    }
    
    /**
     * Создать пользователей из списка
     */
    public void createUsersWithList(List<User> users) {
        restClient.post(users, Void.class, "user", "createWithList");
    }
}
