package storage;

public class StorageManager {
    private static final StorageManager instance = new StorageManager();

    private final UserStorage userStorage = new UserStorage();
    private final GameStorage gameStorage = new GameStorage();
    private final TokenStorage tokenStorage = new TokenStorage();

    private StorageManager() {}

    public static StorageManager getInstance() {
        return instance;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public GameStorage getGameStorage() {
        return gameStorage;
    }

    public TokenStorage getTokenStorage() {
        return tokenStorage;
    }
}
