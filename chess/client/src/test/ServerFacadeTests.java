import serverFacade.ServerFacade;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URISyntaxException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import serverFacade.ServerFacadeException;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static ServerFacade serverFacade;

    @BeforeAll
    public static void setupClass() {
        serverFacade = new ServerFacade("http://localhost:8080");
    }

    @AfterEach // Clear the database after each test
    public void tearDown() throws Exception {
        serverFacade.sendDeleteRequest("/db", null, null);
    }

    private String registerAndLogin() throws IOException, URISyntaxException, ServerFacadeException {
        // Register a new user and log in
        String registerEndpoint = "/user";
        String uniqueUsername = "testUser" + System.currentTimeMillis();
        String registerBody = "{\"username\":\"" + uniqueUsername + "\",\"password\":\"password\",\"email\":\"" + uniqueUsername + "@example.com\"}";
        serverFacade.sendPostRequest(registerEndpoint, registerBody, null);

        String loginEndpoint = "/session";
        String loginBody = "{\"username\":\"" + uniqueUsername + "\",\"password\":\"password\"}";
        String loginResponse = serverFacade.sendPostRequest(loginEndpoint, loginBody, null);
        JsonObject responseObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        return responseObject.get("authToken").getAsString();
    }

    private Integer createGameAndGetId(String authToken) throws IOException, URISyntaxException, ServerFacadeException {
        // Create a new game
        String createGameEndpoint = "/game";
        JsonObject createGameJsonRequest = new JsonObject();
        createGameJsonRequest.addProperty("gameName", "Test Game");
        String createGameResponse = serverFacade.sendPostRequest(createGameEndpoint, createGameJsonRequest.toString(), authToken);
        JsonObject createGameResponseObject = JsonParser.parseString(createGameResponse).getAsJsonObject();
        return createGameResponseObject.get("gameID").getAsInt();
    }

    @Test
    @Order(0)
    @DisplayName("BeforeAll Positive: sendDeleteRequest for database clearing")
    // Note: this is essentially a @BeforeAll but is structured as a test for the purpose of making sure that
    //       the database is cleared and that the method to clear it works as it should
    public void sendDeleteRequestClearDatabaseSuccess() {
        String endpoint = "/db";
        String jsonRequestBody = "{}";

        try {
            String response = serverFacade.sendDeleteRequest(endpoint, jsonRequestBody, null);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("\"success\":true")); // Check for success in the response
        } catch (Exception e) {
            fail("Exception \"" + e.getMessage() + "\" should not be thrown");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Positive: sendPostRequest for User Registration")
    public void sendPostRequestUserRegistrationSuccess() {
        String endpoint = "/user";
        String jsonRequestBody = "{\"username\":\"newUser\",\"password\":\"password\",\"email\":\"new@example.com\"}";

        try {
            String response = serverFacade.sendPostRequest(endpoint, jsonRequestBody, null);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("\"success\":true")); // Check for success in the response
        } catch (Exception e) {
            fail("Exception \"" + e.getMessage() + "\" should not be thrown");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Positive: sendGetRequest for Listing Games")
    public void sendGetRequestListGamesSuccess() throws IOException, URISyntaxException, ServerFacadeException {
        String authToken = registerAndLogin();
        String endpoint = "/game";

        try {
            String response = serverFacade.sendGetRequest(endpoint, authToken);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("games")); // Check for games array in the response
        } catch (Exception e) {
            fail("Exception \"" + e.getMessage() + "\" should not be thrown");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Positive: sendPutRequest for Joining a Game")
    public void sendPutRequestJoinGameSuccess() throws IOException, URISyntaxException, ServerFacadeException {
        String authToken = registerAndLogin();
        int gameId = createGameAndGetId(authToken);
        String endpoint = "/game";
        JsonObject joinGameJsonRequest = new JsonObject();
        joinGameJsonRequest.addProperty("playerColor", "WHITE");
        joinGameJsonRequest.addProperty("gameID", gameId);

        try {
            String joinGameResponse = serverFacade.sendPutRequest(endpoint, joinGameJsonRequest.toString(), authToken);
            assertNotNull(joinGameResponse, "Response should not be null");
            assertTrue(joinGameResponse.contains("\"success\":true")); // Check for success in the response
        } catch (Exception e) {
            fail("Exception \"" + e.getMessage() + "\" should not be thrown");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Positive: sendDeleteRequest for User Logout")
    public void sendDeleteRequestLogoutSuccess() throws IOException, URISyntaxException, ServerFacadeException {
        String authToken = registerAndLogin();
        String endpoint = "/session";
        String jsonRequestBody = "{}"; // Assuming logout does not require a request body

        try {
            String response = serverFacade.sendDeleteRequest(endpoint, jsonRequestBody, authToken);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("\"success\":true")); // Check for success in the response
        } catch (Exception e) {
            fail("Exception \"" + e.getMessage() + "\" should not be thrown");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Negative: sendPostRequest with Invalid Endpoint")
    public void sendPostRequestInvalidEndpoint() {
        String endpoint = "/invalidEndpoint";
        String jsonRequestBody = "{\"key\":\"value\"}";

        assertThrows(ServerFacadeException.class, () -> serverFacade.sendPostRequest(endpoint, jsonRequestBody, null), "ServerFacadeException should be thrown for invalid endpoint");
    }

    @Test
    @Order(6)
    @DisplayName("Negative: sendGetRequest with Invalid Endpoint")
    public void sendGetRequestInvalidEndpoint() throws IOException, URISyntaxException, ServerFacadeException {
        String validAuthToken = registerAndLogin();
        String endpoint = "/invalidEndpoint";

        assertThrows(ServerFacadeException.class, () -> serverFacade.sendGetRequest(endpoint, validAuthToken), "ServerFacadeException should be thrown for invalid endpoint");
    }

    @Test
    @Order(7)
    @DisplayName("Negative: sendPutRequest with Invalid Endpoint")
    public void sendPutRequestInvalidEndpoint() throws IOException, URISyntaxException, ServerFacadeException {
        String validAuthToken = registerAndLogin();
        Integer gameId = createGameAndGetId(validAuthToken);
        String endpoint = "/invalidPutEndpoint";
        JsonObject jsonRequestBody = new JsonObject();
        jsonRequestBody.addProperty("playerColor", "WHITE");
        jsonRequestBody.addProperty("gameID", gameId);

        assertThrows(ServerFacadeException.class, () -> serverFacade.sendPutRequest(endpoint, jsonRequestBody.toString(), validAuthToken), "ServerFacadeException should be thrown for invalid endpoint");
    }

    @Test
    @Order(8)
    @DisplayName("Negative: sendDeleteRequest with Invalid Endpoint")
    public void sendDeleteRequestInvalidEndpoint() throws IOException, URISyntaxException, ServerFacadeException {
        String validAuthToken = registerAndLogin();
        String endpoint = "/invalidDeleteEndpoint";
        String jsonRequestBody = "{}";

        assertThrows(ServerFacadeException.class, () -> serverFacade.sendDeleteRequest(endpoint, jsonRequestBody, validAuthToken), "ServerFacadeException should be thrown for invalid endpoint");
    }
}
