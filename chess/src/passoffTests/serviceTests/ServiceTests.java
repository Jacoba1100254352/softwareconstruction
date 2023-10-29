package passoffTests.serviceTests;

import models.User;
import org.junit.jupiter.api.*;
import requests.*;
import responses.*;
import services.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    private static User existingUser;
    private static String existingAuth;
    private static Integer createdGameID;

    private final JoinGameService joinGameService = new JoinGameService();
    private final ListGamesService listGamesService = new ListGamesService();
    private final LoginService loginService = new LoginService();
    private final LogoutService logoutService = new LogoutService();
    private final RegisterService registerService = new RegisterService();
    private final ClearService clearService = new ClearService();
    private final CreateGameService createGameService = new CreateGameService();

    @BeforeAll
    public static void init() {
        existingUser = new User("Joseph", "Smith", "urim@thummim.net");
    }

    @BeforeEach
    public void setup() {
        clearService.clearDatabase(); // Clear the database before each test

        RegisterRequest registerRequest = new RegisterRequest(existingUser.getUsername(), existingUser.getPassword(), existingUser.getEmail());
        RegisterResponse regResponse = registerService.register(registerRequest);
        existingAuth = regResponse.getAuthToken();

        // Create a game and store its ID for use in join game tests
        CreateGameRequest gameRequest = new CreateGameRequest();
        gameRequest.setGameName("Chess Match");
        gameRequest.setAuthToken(existingAuth);
        CreateGameResponse gameResponse = createGameService.createGame(gameRequest);
        createdGameID = gameResponse.getGameID();
    }

    @Test
    @Order(1)
    @DisplayName("Positive: Successful Database Clear")
    public void successfulDatabaseClear() {
        ClearResponse response = clearService.clearDatabase();
        Assertions.assertTrue(response.isSuccess(), "Failed to clear the database");
    }

    @Test
    @Order(2)
    @DisplayName("Positive: Successful Game Creation")
    public void successfulGameCreation() {
        CreateGameRequest request = new CreateGameRequest();
        request.setGameName("Chess Match");
        request.setAuthToken(existingAuth); // Using the auth token generated during setup
        CreateGameResponse response = createGameService.createGame(request);
        Assertions.assertNotNull(response.getGameID(), "Failed to create game");
    }

    @Test
    @Order(3)
    @DisplayName("Negative: Unauthorized Game Creation")
    public void unauthorizedGameCreation() {
        CreateGameRequest request = new CreateGameRequest();
        request.setGameName("Chess Match");
        request.setAuthToken("invalidAuthToken"); // set an invalid auth token
        CreateGameResponse response = createGameService.createGame(request);
        Assertions.assertEquals("Error: unauthorized", response.getMessage(), "Incorrect error message");
    }

    @Test
    @Order(4)
    @DisplayName("Positive: Successful Game Listing")
    public void successfulGameListing() {
        ListGamesRequest request = new ListGamesRequest();
        request.setAuthToken(existingAuth); // replace with a valid auth token
        ListGamesResponse response = listGamesService.listAllGames(request);
        Assertions.assertNotNull(response.getGames(), "Failed to list games");
    }

    @Test
    @Order(5)
    @DisplayName("Negative: Unauthorized Game Listing")
    public void unauthorizedGameListing() {
        ListGamesRequest request = new ListGamesRequest();
        request.setAuthToken("invalidAuthToken"); // set an invalid auth token
        ListGamesResponse response = listGamesService.listAllGames(request);
        Assertions.assertEquals("Error: unauthorized", response.getMessage(), "Incorrect error message");
    }

    @Test
    @Order(6)
    @DisplayName("Positive: Join Game Successfully")
    public void joinGameSuccess() {
        JoinGameRequest request = new JoinGameRequest();
        request.setGameID(createdGameID);  // Use the ID of the game created during setup
        request.setAuthToken(existingAuth);
        request.setPlayerColor("WHITE");
        JoinGameResponse response = joinGameService.joinGame(request);
        Assertions.assertTrue(response.isSuccess(), "Failed to join the game");
    }

    @Test
    @Order(7)
    @DisplayName("Negative: Invalid Game ID")
    public void invalidGameID() {
        JoinGameRequest request = new JoinGameRequest();
        request.setGameID(-1);  // Setting an invalid game ID to trigger the error
        request.setAuthToken(existingAuth);  // replace with a valid token for testing
        request.setPlayerColor("WHITE");
        JoinGameResponse response = joinGameService.joinGame(request);
        Assertions.assertFalse(response.isSuccess(), "Joined game with an invalid ID");
    }

    @Test
    @Order(8)
    @DisplayName("Positive: Successful Login")
    public void successfulLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername(existingUser.getUsername());  // replace with a valid username for testing
        request.setPassword(existingUser.getPassword());  // replace with a valid password for testing
        LoginResponse response = loginService.login(request);
        Assertions.assertNotNull(response.getAuthToken(), "Failed to login");
    }

    @Test
    @Order(9)
    @DisplayName("Negative: Invalid Credentials")
    public void invalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("invalidUsername");  // set an invalid username
        request.setPassword("invalidPassword");  // set an invalid password
        LoginResponse response = loginService.login(request);
        Assertions.assertEquals("Error: unauthorized", response.getMessage(), "Incorrect error message");
    }

    @Test
    @Order(10)
    @DisplayName("Positive: Successful Logout")
    public void successfulLogout() {
        LogoutRequest request = new LogoutRequest();
        request.setAuthToken(existingAuth);
        LogoutResponse response = logoutService.logout(request);
        Assertions.assertTrue(response.isSuccess(), "Failed to logout");
    }

    @Test
    @Order(11)
    @DisplayName("Negative: Invalid Auth Token")
    public void invalidAuthTokenLogout() {
        LogoutRequest request = new LogoutRequest();
        request.setAuthToken("invalidToken");  // Setting an invalid token to trigger the error
        LogoutResponse response = logoutService.logout(request);
        Assertions.assertEquals("Error: Invalid authentication token.", response.getMessage(), "Incorrect error message");
    }

    @Test
    @Order(12)
    @DisplayName("Positive: Successful Registration")
    public void successfulRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUsername");  // set a new username for testing
        request.setPassword("newPassword");  // set a password for testing
        request.setEmail("newEmail@example.com");  // set an email for testing
        RegisterResponse response = registerService.register(request);
        Assertions.assertNotNull(response.getAuthToken(), "Failed to register");
    }

    @Test
    @Order(13)
    @DisplayName("Negative: Duplicate Username")
    public void duplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(existingUser.getUsername());  // set an existing username to trigger the error
        request.setPassword("password");  // set a password
        request.setEmail("email@example.com");  // set an email
        RegisterResponse response = registerService.register(request);
        Assertions.assertEquals("Error: already taken", response.getMessage(), "Incorrect error message");
    }
}
