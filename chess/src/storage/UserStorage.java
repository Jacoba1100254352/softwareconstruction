package storage;

import models.User;

import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    private final Map<String, User> users = new HashMap<>();

    public boolean containsUser(String username) {
        return users.containsKey(username);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void deleteUser(String username) {
        users.remove(username);
    }

    public void clearUsers() {
        users.clear();
    }
}

