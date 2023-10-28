package storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TokenStorage {

    private final Set<String> validTokens = new HashSet<>();
    private final Map<String, String> tokenToUsernameMap = new HashMap<>();

    public boolean containsToken(String token) {
        return validTokens.contains(token);
    }

    public Set<String> getAllTokens() {
        return new HashSet<>(validTokens);
    }

    public void addToken(String token, String username) {
        validTokens.add(token);
        tokenToUsernameMap.put(token, username);
    }

    public void removeToken(String token) {
        validTokens.remove(token);
        tokenToUsernameMap.remove(token);
    }

    public String getUsernameForToken(String token) {
        return tokenToUsernameMap.get(token);
    }

    public void clearTokens() {
        validTokens.clear();
        tokenToUsernameMap.clear();
    }
}
