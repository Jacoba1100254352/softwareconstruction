package passoffTests.clientTests;

import server.ServerFacade;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.ServerFacadeException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static ServerFacade serverFacade;
    private static String validAuthToken;

    @BeforeAll
    public static void setup() throws IOException, URISyntaxException, ServerFacadeException {
        serverFacade = new ServerFacade("http://localhost:8080");

        // Register a new user
        String registerEndpoint = "/user";
        String registerBody = "{\"username\":\"testUser\",\"password\":\"password\",\"email\":\"test@example.com\"}";
        serverFacade.sendPostRequest(registerEndpoint, registerBody, null);

        // Log in to get a valid token
        String loginEndpoint = "/session";
        String loginBody = "{\"username\":\"testUser\",\"password\":\"password\"}";
        String loginResponse = serverFacade.sendPostRequest(loginEndpoint, loginBody, null);
        JsonObject responseObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        validAuthToken = responseObject.get("authToken").getAsString();

        // Create a new game
        String createGameEndpoint = "/game";
        JsonObject createGameJsonRequest = new JsonObject();
        createGameJsonRequest.addProperty("gameName", "Test Game");
        serverFacade.sendPostRequest(createGameEndpoint, createGameJsonRequest.toString(), validAuthToken);
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
    public void sendGetRequestListGamesSuccess() {
        String endpoint = "/game";

        try {
            String response = serverFacade.sendGetRequest(endpoint, validAuthToken);
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
        // Create a new game first
        String createGameEndpoint = "/game";
        JsonObject createGameJsonRequest = new JsonObject();
        createGameJsonRequest.addProperty("gameName", "Test Game");
        String createGameResponse = serverFacade.sendPostRequest(createGameEndpoint, createGameJsonRequest.toString(), validAuthToken);
        JsonObject createGameResponseObject = JsonParser.parseString(createGameResponse).getAsJsonObject();
        Integer gameId = createGameResponseObject.get("gameID").getAsInt();

        // Now try to join the game
        String joinGameEndpoint = "/game";
        JsonObject joinGameJsonRequest = new JsonObject();
        joinGameJsonRequest.addProperty("playerColor", "WHITE");
        joinGameJsonRequest.addProperty("gameID", gameId);

        try {
            String joinGameResponse = serverFacade.sendPutRequest(joinGameEndpoint, joinGameJsonRequest.toString(), validAuthToken);
            assertNotNull(joinGameResponse, "Response should not be null");
            assertTrue(joinGameResponse.contains("\"success\":true")); // Check for success in the response
        } catch (Exception e) {
            fail("Exception \"" + e.getMessage() + "\" should not be thrown");
        }
    }


    @Test
    @Order(4)
    @DisplayName("Positive: sendDeleteRequest for User Logout")
    public void sendDeleteRequestLogoutSuccess() {
        String endpoint = "/session";
        String jsonRequestBody = "{}"; // Assuming logout does not require a request body

        try {
            String response = serverFacade.sendDeleteRequest(endpoint, jsonRequestBody, validAuthToken);
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

        try {
            String response = serverFacade.sendPostRequest(endpoint, jsonRequestBody, validAuthToken);
            fail("Expected IOException was not thrown");
        } catch (ServerFacadeException e) {
            assertNotNull(e.getMessage(), "Exception message should not be null");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Negative: sendGetRequest with Invalid Endpoint")
    public void sendGetRequestInvalidEndpoint() {
        String endpoint = "/invalidEndpoint";

        try {
            String response = serverFacade.sendGetRequest(endpoint, validAuthToken);
            fail("Expected IOException was not thrown");
        } catch (ServerFacadeException e) {
            assertNotNull(e.getMessage(), "Exception message should not be null");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Negative: sendPutRequest with Invalid Endpoint")
    public void sendPutRequestInvalidEndpoint() {
        String endpoint = "/invalidPutEndpoint";
        String jsonRequestBody = "{\"key\":\"newValue\"}";

        try {
            String response = serverFacade.sendPutRequest(endpoint, jsonRequestBody, validAuthToken);
            fail("Expected IOException was not thrown");
        } catch (ServerFacadeException e) {
            assertNotNull(e.getMessage(), "Exception message should not be null");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Negative: sendDeleteRequest with Invalid Endpoint")
    public void sendDeleteRequestInvalidEndpoint() {
        String endpoint = "/invalidDeleteEndpoint";
        String jsonRequestBody = "{\"key\":\"valueToDelete\"}";

        try {
            String response = serverFacade.sendDeleteRequest(endpoint, jsonRequestBody, validAuthToken);
            fail("Expected IOException was not thrown");
        } catch (ServerFacadeException e) {
            assertNotNull(e.getMessage(), "Exception message should not be null");
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName());
        }
    }
}
