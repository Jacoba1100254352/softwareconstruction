package storage;

import models.AuthToken;

import java.util.HashMap;

public class TokenStorage {
    private final HashMap<String, AuthToken> tokens = new HashMap<>();

    public void addToken(AuthToken authToken) {
        tokens.put(authToken.getToken(), authToken);
    }

    public AuthToken getToken(String authTokenString) {
        return tokens.get(authTokenString);
    }

    public void removeToken(AuthToken authToken) {
        tokens.remove(authToken.getToken());
    }

    public boolean containsToken(String token) {
        return tokens.containsKey(token);
    }

    public void clearTokens() {
        tokens.clear();
    }
}
