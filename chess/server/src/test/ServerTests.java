import chess.*;
import dataAccess.*;
import models.AuthToken;
import models.Game;
import models.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class ServerTests {
    private static Database db;
    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        db = new Database();
        db.resetDatabase(); // Resetting the database to a clean state before tests
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
    }

    @NotNull
    private static ChessGame getChessGame(ChessGame originalGame) throws InvalidMoveException {
        // Simulate a move to change the state of the game.
        ChessPosition startPosition = new ChessPositionImpl(2, 5);
        ChessPosition endPosition = new ChessPositionImpl(4, 5);
        originalGame.makeMove(new ChessMoveImpl(startPosition, endPosition, null));

        // Return the updated game state.
        return originalGame;
    }

    @BeforeEach
    public void setUpEach() throws DataAccessException {
        db.resetDatabase(); // Resetting the database to a clean state before each test
    }

    @AfterEach
    public void tearDownEach() throws DataAccessException {
        db.resetDatabase(); // Cleans up the database after each test
    }


    ///   AuthDAOTest   ///

    // Positive Test for insertAuth
    @Test
    @Order(1)
    @DisplayName("Positive: insertAuth")
    public void insertAuthPass() throws DataAccessException {
        // Create
        userDAO.insertUser(new User("testUser", "password", "test@example.com"));

        // Apply
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);

        // Assert
        Assertions.assertNotNull(authDAO.findAuth(token.getToken()));
    }

    // Negative Test for insertAuth (attempting to insert a duplicate)
    @Test
    @Order(2)
    @DisplayName("Negative: insertAuth")
    public void insertAuthFail() throws DataAccessException {
        // Create
        userDAO.insertUser(new User("testUser", "password", "test@example.com"));

        // Apply
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);

        // Assert
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.insertAuth(token));
    }

    // Positive Test for findAuth
    @Test
    @Order(3)
    @DisplayName("Positive: findAuth")
    public void findAuthPass() throws DataAccessException {
        // Create
        userDAO.insertUser(new User("testUser", userDAO.hashPassword("password"), "test@example.com"));

        // Apply
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);

        // Assert
        Assertions.assertEquals(token.getUsername(), authDAO.findAuth(token.getToken()).getUsername());
    }

    // Negative Test for findAuth (token doesn't exist)
    @Test
    @Order(4)
    @DisplayName("Negative: findAuth")
    public void findAuthFail() throws DataAccessException {
        // Assert
        Assertions.assertNull(authDAO.findAuth("nonExistingToken"));
    }

    // Positive Test for deleteAuth
    @Test
    @Order(5)
    @DisplayName("Positive: deleteAuth")
    public void deleteAuthPass() throws DataAccessException {
        // Create
        userDAO.insertUser(new User("testUser", userDAO.hashPassword("password"), "test@example.com"));


        // Apply
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);
        authDAO.deleteAuth(token);

        // Assert
        Assertions.assertNull(authDAO.findAuth(token.getToken()));
    }


    ///   UserDAO   ///

    // Negative Test for deleteAuth (token doesn't exist)
    @Test
    @Order(6)
    @DisplayName("Negative: deleteAuth")
    public void deleteAuthFail() {
        // Create
        AuthToken token = new AuthToken("nonExistingToken", "testUser");

        // Assert
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(token));
    }

    // Positive Test for insertUser
    @Test
    @Order(7)
    @DisplayName("Positive: insertUser")
    public void insertUserPass() throws DataAccessException {
        // Create
        User user = new User("testUser", "password", "test@example.com");

        // Apply
        userDAO.insertUser(user);

        // Assert
        Assertions.assertNotNull(userDAO.getUser(user.getUsername()));
    }

    // Negative Test for insertUser (attempting to insert a user with an existing username)
    @Test
    @Order(8)
    @DisplayName("Negative: insertUser")
    public void insertUserFail() throws DataAccessException {
        // Create
        String uniqueUsername = "uniqueTestUser" + System.currentTimeMillis();

        // Apply
        User user = new User(uniqueUsername, "password", uniqueUsername + "@example.com");
        userDAO.insertUser(user);

        // Assert
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insertUser(user));
    }

    // Positive Test for validatePassword
    @Test
    @Order(9)
    @DisplayName("Positive: validatePassword")
    public void validatePasswordPass() throws DataAccessException {
        // Create
        User user = new User("testUser", "password", "test@example.com");

        // Apply
        userDAO.insertUser(user);

        // Assert
        Assertions.assertTrue(userDAO.validatePassword(user.getUsername(), "password"));
    }

    // Negative Test for validatePassword (wrong password)
    @Test
    @Order(10)
    @DisplayName("Negative: validatePassword")
    public void validatePasswordFail() throws DataAccessException {
        // Create
        User user = new User("testUser", "password", "test@example.com");

        // Apply
        userDAO.insertUser(user);

        // Assert
        Assertions.assertFalse(userDAO.validatePassword(user.getUsername(), "wrongPassword"));
    }

    // Positive Test for getUser
    @Test
    @Order(11)
    @DisplayName("Positive: getUser")
    public void getUserPass() throws DataAccessException {
        // Create
        User user = new User("testUser", "password", "test@example.com");

        // Apply
        userDAO.insertUser(user);

        // Assert
        Assertions.assertEquals(user.getUsername(), userDAO.getUser(user.getUsername()).getUsername());
    }

    // Negative Test for getUser (user doesn't exist)
    @Test
    @Order(12)
    @DisplayName("Negative: getUser")
    public void getUserFail() throws DataAccessException {
        // Assert
        Assertions.assertNull(userDAO.getUser("nonExistingUser"));
    }

    // Positive Test for updateUser
    @Test
    @Order(13)
    @DisplayName("Positive: updateUser")
    public void updateUserPass() throws DataAccessException {
        // Create
        User user = new User("testUser", "password", "test@example.com");

        // Apply
        userDAO.insertUser(user);
        userDAO.updateUser(new User("testUser", "newPassword", "newtest@example.com"));

        // Assert
        Assertions.assertEquals("newtest@example.com", userDAO.getUser(user.getUsername()).getEmail());
    }

    // Negative Test for updateUser (user doesn't exist)
    @Test
    @Order(14)
    @DisplayName("Negative: updateUser")
    public void updateUserFail() {
        // Create
        User user = new User("nonExistingUser", "password", "test@example.com");

        // Assert
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.updateUser(user));
    }

    // Positive Test for deleteUser
    @Test
    @Order(15)
    @DisplayName("Positive: deleteUser")
    public void deleteUserPass() throws DataAccessException, SQLException {
        // Create
        User user = new User("testUser", "password", "test@example.com");

        // Apply
        userDAO.insertUser(user);
        userDAO.deleteUser(user.getUsername());

        // Assert
        Assertions.assertNull(userDAO.getUser(user.getUsername()));
    }


    ///   GameDAO   ///

    // Negative Test for deleteUser (user doesn't exist)
    @Test
    @Order(16)
    @DisplayName("Negative: deleteUser")
    public void deleteUserFail() {
        // Assert
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.deleteUser("nonExistingUser"));
    }

    // Positive Test for insertGame
    @Test
    @Order(17)
    @DisplayName("Positive: insertGame")
    public void insertGamePass() throws DataAccessException {
        // Create a new game with unique data
        String gameName = "testGamePass";
        Game game = new Game(null, gameName, null, null, new ChessGameImpl());

        // Insert the game
        gameDAO.insertGame(game);

        // Retrieve the game by name to check if it was inserted correctly
        Game insertedGame = gameDAO.findGameByID(game.getGameID());
        Assertions.assertNotNull(insertedGame);
        Assertions.assertEquals(gameName, insertedGame.getGameName());
    }

    // Negative Test for insertGame (attempting to insert a game with non-existent user)
    @Test
    @Order(18)
    @DisplayName("Negative: insertGame")
    public void insertGameFail() {
        // Create
        Game game = new Game(null, "testGameFail", "nonExistentUser", "nonExistentUser", new ChessGameImpl());

        // Assert
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.insertGame(game));
    }

    // Positive Test for findGameById
    @Test
    @Order(19)
    @DisplayName("Positive: findGame")
    public void findGameByIdPass() throws DataAccessException {
        // Create a new game with unique data
        Game game = new Game(null, "testFindGame", null, null, new ChessGameImpl());

        // Insert the game and retrieve it by ID
        gameDAO.insertGame(game);
        Game retrievedGame = gameDAO.findGameByID(game.getGameID());

        // Assert
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals(game.getGameID(), retrievedGame.getGameID());
    }

    // Negative Test for findGameById (game doesn't exist)
    @Test
    @Order(20)
    @DisplayName("Negative: findGame")
    public void findGameByIdFail() throws DataAccessException {
        // Find id not associated yet with a game in the database
        Integer id = 0;
        while (gameDAO.findGameByID(++id) != null) ;

        // Test search for non-existing game
        Assertions.assertNull(gameDAO.findGameByID(9999));
    }

    // Positive Test for findAllGames
    @Test
    @Order(21)
    @DisplayName("Positive: findAllGames")
    public void findAllGamesPass() throws DataAccessException {
        // Insert multiple games
        for (int i = 0; i < 3; i++)
            gameDAO.insertGame(new Game(null, "testGame" + i, null, null, new ChessGameImpl()));

        // Retrieve all games and assert that the count is correct
        Assertions.assertEquals(3, gameDAO.findAllGames().size());
    }

    // Positive Test for claimSpot
    @Test
    @Order(22)
    @DisplayName("Positive: claimSpot")
    public void claimSpotPass() throws DataAccessException {
        String testUser = "testPlayer";

        // Insert a new user for testing
        userDAO.insertUser(new User(testUser, userDAO.hashPassword("password"), "testPlayer@example.com"));

        // Insert a new game
        Game game = new Game(null, "testClaimSpot", null, null, new ChessGameImpl());
        gameDAO.insertGame(game);

        // Claim a spot
        gameDAO.claimSpot(game.getGameID(), testUser, ChessGame.TeamColor.WHITE);

        // Retrieve the game and assert that the spot has been claimed
        Game updatedGame = gameDAO.findGameByID(game.getGameID());
        Assertions.assertEquals(testUser, updatedGame.getWhiteUsername());
    }

    // Negative Test for claimSpot (spot already claimed)
    @Test
    @Order(23)
    @DisplayName("Negative: claimSpot")
    public void claimSpotFail() throws DataAccessException {
        String firstPlayer = "testUser";
        String secondPlayer = "anotherUser";

        // Inserting the user first
        userDAO.insertUser(new User(firstPlayer, userDAO.hashPassword("password"), "test@example.com"));
        // Inserting another user
        userDAO.insertUser(new User(secondPlayer, userDAO.hashPassword("password"), "another@example.com"));

        Game game = new Game(null, "testGame", firstPlayer, null, new ChessGameImpl());

        gameDAO.insertGame(game);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.claimSpot(game.getGameID(), secondPlayer, ChessGame.TeamColor.WHITE));
    }

    // Positive Test for updateGame
    @Test
    @Order(24)
    @DisplayName("Positive: updateGame")
    public void updateGamePass() throws DataAccessException, InvalidMoveException {
        // Setup players and ensure the users exist in the database
        userDAO.insertUser(new User("whitePlayer", userDAO.hashPassword("password"), "test1@example.com"));
        userDAO.insertUser(new User("blackPlayer", userDAO.hashPassword("password"), "test2@example.com"));

        // Setup game
        ChessGame originalGame = new ChessGameImpl();
        originalGame.getBoard().resetBoard();
        originalGame.setTeamTurn(ChessGame.TeamColor.WHITE);

        // Insert Game
        Game game = new Game(null, "game1", "whitePlayer", "blackPlayer", originalGame);
        gameDAO.insertGame(game);

        // Make a move to change the game state
        ChessGame newGame = getChessGame(originalGame);
        game.setGame(newGame);
        gameDAO.updateGame(game);

        // Assert
        Assertions.assertEquals(gameDAO.serializeChessGame(newGame), gameDAO.serializeChessGame(gameDAO.findGameByID(game.getGameID()).getChessGame()), "Game state did not update correctly in the database.");
    }

    // Negative Test for updateGame (game does not exist)
    @Test
    @Order(25)
    @DisplayName("Negative: updateGame")
    public void updateGameFail() throws DataAccessException {
        // Find id not associated yet with a game in the database
        Integer id = 0;
        while (gameDAO.findGameByID(++id) != null) ;

        // Create a game object for a non-existing game
        Game nonExistentGame = new Game(id, "NonExistentGame", "whitePlayer", "blackPlayer", new ChessGameImpl());

        // Pass the non-existent game object
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(nonExistentGame));
    }

    // Positive Test for getCurrentGameId
    @Test
    @Order(26)
    @DisplayName("Positive: getCurrentGameId")
    public void getCurrentGameIdPass() throws DataAccessException {
        Game game = new Game(null, "testGame", null, null, new ChessGameImpl());
        gameDAO.insertGame(game);
        Assertions.assertEquals(game.getGameID(), gameDAO.getCurrentGameId());
    }

    // Negative Test for getCurrentGameId
    @Test
    @Order(27)
    @DisplayName("Negative: getCurrentGameId with no games")
    public void getCurrentGameIdFailNoGames() {
        // Act and Assert // Test should pass if a DataAccessException is thrown because there are no games
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getCurrentGameId(), "Expected DataAccessException to be thrown when there are no games in the database.");
    }


    // Positive Test for clearGames
    @Test
    @Order(28)
    @DisplayName("Positive: clearGames")
    public void clearGamesPass() throws DataAccessException, SQLException {
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);

            gameDAO.insertGame(new Game(null, "testGame", null, null, new ChessGameImpl()));
            gameDAO.clearGames(conn);

            conn.commit();

            Assertions.assertTrue(gameDAO.findAllGames().isEmpty());
        } catch (DataAccessException dae) {
            conn.rollback();
            throw dae;
        } finally {
            db.closeConnection(conn);
        }
    }
}
