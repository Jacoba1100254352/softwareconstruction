package passoffTests.clientTests;

import server.ServerFacade;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private ServerFacade serverFacade;

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade("http://localhost:8080");
    }

    @Test
    @Order(1)
    @DisplayName("Positive: sendPostRequest")
    public void sendPostRequestSuccess() {
        String endpoint = "/testPost";
        String jsonRequestBody = "{\"key\":\"value\"}";

        try {
            String response = serverFacade.sendPostRequest(endpoint, jsonRequestBody);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("expected content")); // Replace with actual expected content
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Negative: sendPostRequest with Invalid Endpoint")
    public void sendPostRequestFail() {
        String endpoint = "/invalidEndpoint";
        String jsonRequestBody = "{\"key\":\"value\"}";

        assertThrows(IOException.class, () -> serverFacade.sendPostRequest(endpoint, jsonRequestBody), "IOException should be thrown for invalid endpoint");
    }

    @Test
    @Order(3)
    @DisplayName("Positive: sendGetRequest")
    public void sendGetRequestSuccess() {
        String endpoint = "/testGet";

        try {
            String response = serverFacade.sendGetRequest(endpoint);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("expected content")); // Replace with actual expected content
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Negative: sendGetRequest with Invalid Endpoint")
    public void sendGetRequestFail() {
        String endpoint = "/invalidEndpoint";

        assertThrows(IOException.class, () -> serverFacade.sendGetRequest(endpoint), "IOException should be thrown for invalid endpoint");
    }

    @Test
    @Order(5)
    @DisplayName("Positive: sendPutRequest")
    public void sendPutRequestSuccess() {
        String endpoint = "/testPut";
        String jsonRequestBody = "{\"key\":\"newValue\"}";

        try {
            String response = serverFacade.sendPutRequest(endpoint, jsonRequestBody);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("expected updated content")); // Replace with actual expected content
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Negative: sendPutRequest with Invalid Endpoint")
    public void sendPutRequestFail() {
        String endpoint = "/invalidPutEndpoint";
        String jsonRequestBody = "{\"key\":\"newValue\"}";

        assertThrows(IOException.class, () -> serverFacade.sendPutRequest(endpoint, jsonRequestBody), "IOException should be thrown for invalid endpoint");
    }

    @Test
    @Order(7)
    @DisplayName("Positive: sendDeleteRequest")
    public void sendDeleteRequestSuccess() {
        String endpoint = "/testDelete";
        String jsonRequestBody = "{\"key\":\"valueToDelete\"}";

        try {
            String response = serverFacade.sendDeleteRequest(endpoint, jsonRequestBody);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.contains("expected deletion confirmation")); // Replace with actual expected content
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Negative: sendDeleteRequest with Invalid Endpoint")
    public void sendDeleteRequestFail() {
        String endpoint = "/invalidDeleteEndpoint";
        String jsonRequestBody = "{\"key\":\"valueToDelete\"}";

        assertThrows(IOException.class, () -> serverFacade.sendDeleteRequest(endpoint, jsonRequestBody), "IOException should be thrown for invalid endpoint");
    }

}
