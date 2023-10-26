package storage;

import models.User;

import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    private final Map<String, User> users = new HashMap<>();

    public Map<String, User> getUsers() {
        return users;
    }
}
