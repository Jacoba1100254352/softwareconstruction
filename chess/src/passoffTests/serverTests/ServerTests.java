package passoffTests.serverTests;

import chess.*;
import dataAccess.*;
import models.AuthToken;
import models.Game;
import models.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

    private void insertTestUser(String username, String email) throws DataAccessException {
        String hashedPassword = userDAO.hashPassword("password");
        User user = new User(username, hashedPassword, email);
        userDAO.insertUser(user);
    }

    @BeforeEach
    public void setUpEach() throws DataAccessException {
        db.resetDatabase(); // Resetting the database to a clean state before each test
    }

    ///   AuthDAOTest   ///

    @AfterEach
    public void tearDownEach() throws DataAccessException {
        db.resetDatabase(); // Cleans up the database after each test
    }

    // Positive Test for insertAuth
    @Test
    @Order(1)
    @DisplayName("Positive: insertAuth")
    public void insertAuthPass() throws DataAccessException {
        // First, insert a user
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);

        // Then, create and insert the AuthToken
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);
        Assertions.assertNotNull(authDAO.findAuth(token.getToken()));
    }

    // Negative Test for insertAuth (attempting to insert a duplicate)
    @Test
    @Order(2)
    @DisplayName("Negative: insertAuth")
    public void insertAuthFail() throws DataAccessException {
        // First, ensure the user exists
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);

        // Then, insert the first AuthToken
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);

        // Attempt to insert the same AuthToken again and expect an exception
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.insertAuth(token); // Attempting to insert the same token again
        });
    }

    // Positive Test for findAuth
    @Test
    @Order(3)
    @DisplayName("Positive: findAuth")
    public void findAuthPass() throws DataAccessException {
        insertTestUser("testUser", "test@example.com"); // Inserting the user first

        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);
        AuthToken foundToken = authDAO.findAuth(token.getToken());
        Assertions.assertEquals(token.getUsername(), foundToken.getUsername());
    }

    // Negative Test for findAuth (token doesn't exist)
    @Test
    @Order(4)
    @DisplayName("Negative: findAuth")
    public void findAuthFail() throws DataAccessException {
        Assertions.assertNull(authDAO.findAuth("nonExistingToken"));
    }

    // Positive Test for deleteAuth
    @Test
    @Order(5)
    @DisplayName("Positive: deleteAuth")
    public void deleteAuthPass() throws DataAccessException {
        insertTestUser("testUser", "test@example.com"); // Inserting the user first

        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);
        authDAO.deleteAuth(token);
        Assertions.assertNull(authDAO.findAuth(token.getToken()));
    }


    ///   UserDAO   ///

    // Negative Test for deleteAuth (token doesn't exist)
    @Test
    @Order(6)
    @DisplayName("Negative: deleteAuth")
    public void deleteAuthFail() {
        AuthToken token = new AuthToken("nonExistingToken", "testUser");
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(token));
    }

    // Positive Test for insertUser
    @Test
    @Order(7)
    @DisplayName("Positive: insertUser")
    public void insertUserPass() throws DataAccessException {
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);
        Assertions.assertNotNull(userDAO.getUser(user.getUsername()));
    }

    // Negative Test for insertUser (attempting to insert a user with an existing username)
    @Test
    @Order(8)
    @DisplayName("Negative: insertUser")
    public void insertUserFail() throws DataAccessException {
        String uniqueUsername = "uniqueTestUser" + System.currentTimeMillis(); // Ensures uniqueness
        User user = new User(uniqueUsername, "password", uniqueUsername + "@example.com");
        userDAO.insertUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.insertUser(user); // This should fail
        });
    }

    // Positive Test for validatePassword
    @Test
    @Order(9)
    @DisplayName("Positive: validatePassword")
    public void validatePasswordPass() throws DataAccessException {
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);
        Assertions.assertTrue(userDAO.validatePassword(user.getUsername(), "password"));
    }

    // Negative Test for validatePassword (wrong password)
    @Test
    @Order(10)
    @DisplayName("Negative: validatePassword")
    public void validatePasswordFail() throws DataAccessException {
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);
        Assertions.assertFalse(userDAO.validatePassword(user.getUsername(), "wrongPassword"));
    }

    // Positive Test for getUser
    @Test
    @Order(11)
    @DisplayName("Positive: getUser")
    public void getUserPass() throws DataAccessException {
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);
        User foundUser = userDAO.getUser(user.getUsername());
        Assertions.assertEquals(user.getUsername(), foundUser.getUsername());
    }

    // Negative Test for getUser (user doesn't exist)
    @Test
    @Order(12)
    @DisplayName("Negative: getUser")
    public void getUserFail() throws DataAccessException {
        Assertions.assertNull(userDAO.getUser("nonExistingUser"));
    }

    // Positive Test for updateUser
    @Test
    @Order(13)
    @DisplayName("Positive: updateUser")
    public void updateUserPass() throws DataAccessException {
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);
        user.setPassword("newPassword");
        user.setEmail("newtest@example.com");
        userDAO.updateUser(user);
        User updatedUser = userDAO.getUser(user.getUsername());
        Assertions.assertEquals("newtest@example.com", updatedUser.getEmail());
    }

    // Negative Test for updateUser (user doesn't exist)
    @Test
    @Order(14)
    @DisplayName("Negative: updateUser")
    public void updateUserFail() {
        User user = new User("nonExistingUser", "password", "test@example.com");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.updateUser(user));
    }

    // Positive Test for deleteUser
    @Test
    @Order(15)
    @DisplayName("Positive: deleteUser")
    public void deleteUserPass() throws DataAccessException, SQLException {
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);
        userDAO.deleteUser(user.getUsername());
        Assertions.assertNull(userDAO.getUser(user.getUsername()));
    }


    ///   GameDAO   ///

    // Negative Test for deleteUser (user doesn't exist)
    @Test
    @Order(16)
    @DisplayName("Negative: deleteUser")
    public void deleteUserFail() {
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.deleteUser("nonExistingUser"));
    }

    // Positive Test for insertGame
    @Test
    @Order(17)
    @DisplayName("Positive: insertGame")
    public void insertGamePass() throws DataAccessException {
        // Assume a new game with unique data to be inserted
        String gameName = "testGamePass";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(null, gameName); // GameID is null before insertion
        game.setGame(chessGame); // Setting the chess game

        // Insert the game
        gameDAO.insertGame(game);

        // Now, we retrieve the game by name to check if it was inserted correctly
        Game insertedGame = gameDAO.findGameById(game.getGameID());
        Assertions.assertNotNull(insertedGame);
        Assertions.assertEquals(gameName, insertedGame.getGameName());
    }

    // Negative Test for insertGame (attempting to insert a game with non-existent user)
    @Test
    @Order(18)
    @DisplayName("Negative: insertGame")
    public void insertGameFail() throws DataAccessException {
        // Arrange
        String gameName = "testGameFail";
        ChessGame chessGame = new ChessGameImpl();
        Game game = new Game(null, gameName, "nonExistentUser", "nonExistentUser", chessGame);

        // Act & Assert
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.insertGame(game));
    }

    // Positive Test for findGameById
    @Test
    @Order(19)
    @DisplayName("Positive: findGameById")
    public void findGameByIdPass() throws DataAccessException {
        // Assume a new game with unique data to be inserted
        String gameName = "testFindGame";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(null, gameName); // GameID is null before insertion
        game.setGame(chessGame); // Setting the chess game

        // Insert the game and retrieve it by ID
        gameDAO.insertGame(game);
        Game retrievedGame = gameDAO.findGameById(game.getGameID());

        // Assertions
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals(game.getGameID(), retrievedGame.getGameID());
    }

    // Negative Test for findGameById (game doesn't exist)
    @Test
    @Order(20)
    @DisplayName("Negative: findGame")
    public void findGameByIdFail() throws DataAccessException {
        Assertions.assertNull(gameDAO.findGameById(9999)); // Assuming this ID doesn't exist
    }

    // Positive Test for findAllGames
    @Test
    @Order(21)
    @DisplayName("Positive: findAllGames")
    public void findAllGamesPass() throws DataAccessException {
        // Insert multiple games
        for (int i = 0; i < 3; i++) {
            Game game = new Game(null, "testGame" + i);
            game.setGame(new ChessGameImpl()); // Setting the chess game
            gameDAO.insertGame(game);
        }

        // Retrieve all games and assert that the count is correct
        List<Game> games = gameDAO.findAllGames();
        Assertions.assertEquals(3, games.size());
    }

    // Positive Test for claimSpot
    @Test
    @Order(22)
    @DisplayName("Positive: claimSpot")
    public void claimSpotPass() throws DataAccessException {
        // Insert a new user for testing
        insertTestUser("testPlayer", "testPlayer@example.com");

        // Insert a new game
        String gameName = "testClaimSpot";
        Game game = new Game(null, gameName);
        game.setGame(new ChessGameImpl());
        gameDAO.insertGame(game);

        // Claim a spot
        gameDAO.claimSpot(game.getGameID(), "testPlayer", ChessGame.TeamColor.WHITE);

        // Retrieve the game and assert that the spot has been claimed
        Game updatedGame = gameDAO.findGameById(game.getGameID());
        Assertions.assertEquals("testPlayer", updatedGame.getWhiteUsername());
    }

    // Negative Test for claimSpot (spot already claimed)
    @Test
    @Order(23)
    @DisplayName("Negative: claimSpot")
    public void claimSpotFail() throws DataAccessException {
        insertTestUser("testUser", "test@example.com"); // Inserting the user first
        insertTestUser("anotherUser", "another@example.com"); // Inserting another user

        Integer gameID = 6; // Unique ID
        String gameName = "testGame";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(gameID, gameName); // Using the correct constructor
        game.setGame(chessGame); // Setting the chess game
        game.setWhiteUsername("testUser");
        gameDAO.insertGame(game);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.claimSpot(game.getGameID(), "anotherUser", ChessGame.TeamColor.WHITE));
    }

    // Positive Test for updateGame
    @Test
    @Order(24)
    @DisplayName("Positive: updateGame")
    public void updateGamePass() throws DataAccessException, InvalidMoveException {
        // Setup player
        String whiteUsername = "whitePlayer";
        String blackUsername = "blackPlayer";
        String whiteEmail = "test1@example.com";
        String blackEmail = "test2@example.com";

        // Ensure the users exist in the database
        insertTestUser(whiteUsername, whiteEmail);
        insertTestUser(blackUsername, blackEmail);

        // Setup game
        ChessGame originalGame = new ChessGameImpl();
        originalGame.getBoard().resetBoard();
        originalGame.setTeamTurn(ChessGame.TeamColor.WHITE);

        // Insert Game
        Game game = new Game(null, "game1", whiteUsername, blackUsername, originalGame);
        gameDAO.insertGame(game); // insertGame now sets the gameID on the game object

        // Act
        // Make a move to change the game state
        ChessGame newGame = getChessGame(originalGame);
        game.setGame(newGame); // Update the game object with the new game state
        gameDAO.updateGame(game); // Pass the game object with the updated state to updateGame

        // Assert
        Game gameAfterUpdate = gameDAO.findGameById(game.getGameID()); // Use the gameID from the inserted game
        Assertions.assertEquals(gameDAO.serializeChessGame(newGame), gameDAO.serializeChessGame(gameAfterUpdate.getGame()), "Game state did not update correctly in the database.");
    }

    // Negative Test for updateGame (game does not exist)
    @Test
    @Order(25)
    @DisplayName("Negative: updateGame")
    public void updateGameFail() {
        ChessGame newChessGame = new ChessGameImpl(); // Correct instantiation
        Game nonExistentGame = new Game(9999, "NonExistentGame", "whitePlayer", "blackPlayer", newChessGame); // Assuming ID 9999 does not exist

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(nonExistentGame); // Pass the non-existent game object
        });
    }

    // Positive Test for getCurrentGameId
    @Test
    @Order(26)
    @DisplayName("Positive: getCurrentGameId")
    public void getCurrentGameIdPass() throws DataAccessException {
        Integer gameID = 8; // Unique ID
        String gameName = "testGame";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(gameID, gameName); // Using the correct constructor
        game.setGame(chessGame); // Setting the chess game
        gameDAO.insertGame(game);
        Integer mostRecentId = gameDAO.getCurrentGameId();
        Assertions.assertEquals(game.getGameID(), mostRecentId);
    }

    // Positive Test for clearGames
    @Test
    @Order(27)
    @DisplayName("Positive: clearGames")
    public void clearGamesPass() throws DataAccessException, SQLException {
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);
            Integer gameID = 9; // Unique ID
            String gameName = "testGame";
            ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
            Game game = new Game(gameID, gameName); // Using the correct constructor
            game.setGame(chessGame); // Setting the chess game
            gameDAO.insertGame(game);
            gameDAO.clearGames(conn);
            conn.commit();
            List<Game> games = gameDAO.findAllGames();
            Assertions.assertTrue(games.isEmpty());
        } catch (DataAccessException dae) {
            conn.rollback();
            throw dae;
        } finally {
            db.closeConnection(conn);
        }
    }
}
