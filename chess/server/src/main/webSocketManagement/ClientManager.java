package webSocketManagement;

public interface ClientManager {
    void add(Integer gameID, ClientInstance instance);

    void remove(String username, Integer gameID);

    void removeAll();
}
