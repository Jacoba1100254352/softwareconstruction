package serverFacade;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String sendPostRequest(String endpoint, String jsonRequestBody, String authToken) throws IOException, URISyntaxException, ServerFacadeException {
        return sendHttpRequest(endpoint, "POST", jsonRequestBody, authToken);
    }

    public String sendGetRequest(String endpoint, String authToken) throws IOException, URISyntaxException, ServerFacadeException {
        return sendHttpRequest(endpoint, "GET", null, authToken);
    }

    public String sendPutRequest(String endpoint, String jsonRequestBody, String authToken) throws IOException, URISyntaxException, ServerFacadeException {
        return sendHttpRequest(endpoint, "PUT", jsonRequestBody, authToken);
    }

    public String sendDeleteRequest(String endpoint, String jsonRequestBody, String authToken) throws IOException, URISyntaxException, ServerFacadeException {
        return sendHttpRequest(endpoint, "DELETE", jsonRequestBody, authToken);
    }

    private String sendHttpRequest(String endpoint, String method, String jsonRequestBody, String authToken)
            throws IOException, URISyntaxException, ServerFacadeException {
        URL url = new URI(serverUrl + endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set up the request
        conn.setRequestMethod(method);
        if (authToken != null && !authToken.isEmpty()) {
            conn.setRequestProperty("Authorization", authToken);
        }
        if (jsonRequestBody != null && !jsonRequestBody.isEmpty()) {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        // Read the response
        try {
            return readResponse(conn);
        } catch (IOException e) {
            throw new ServerFacadeException("HTTP response code: " + conn.getResponseCode(), e);
        }
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
}
