package passoffTests.serverTests;

import chess.ChessGameImpl;
import dataAccess.*;
import models.*;
import org.junit.jupiter.api.*;
import chess.ChessGame;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServerTests {
    private static Database db;
    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static ChessGame chessGame;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        db = new Database();
        db.resetDatabase(); // Resetting the database to a clean state before tests
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        chessGame = new ChessGameImpl();
    }


    ///   AuthDAOTest   ///

    // Positive Test for insertAuth
    @Test
    @Order(1)
    @DisplayName("Positive: insertAuth")
    public void insertAuthPass() throws DataAccessException {
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);
        Assertions.assertNotNull(authDAO.findAuth(token.getToken()));
    }

    // Negative Test for insertAuth (attempting to insert a duplicate)
    @Test
    @Order(2)
    @DisplayName("Negative: insertAuth")
    public void insertAuthFail() throws DataAccessException {
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.insertAuth(token); // Attempting to insert the same token again
        });
    }

    // Positive Test for findAuth
    @Test
    @Order(3)
    @DisplayName("Positive: findAuth")
    public void findAuthPass() throws DataAccessException {
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
        AuthToken token = new AuthToken("12345", "testUser");
        authDAO.insertAuth(token);
        authDAO.deleteAuth(token);
        Assertions.assertNull(authDAO.findAuth(token.getToken()));
    }

    // Negative Test for deleteAuth (token doesn't exist)
    @Test
    @Order(6)
    @DisplayName("Negative: deleteAuth")
    public void deleteAuthFail() throws DataAccessException {
        AuthToken token = new AuthToken("nonExistingToken", "testUser");
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth(token);
        });
    }


    ///   UserDAO   ///

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
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.insertUser(user); // Attempting to insert the same user again
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
    public void updateUserFail() throws DataAccessException {
        User user = new User("nonExistingUser", "password", "test@example.com");
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.updateUser(user);
        });
    }

    // Positive Test for deleteUser
    @Test
    @Order(15)
    @DisplayName("Positive: deleteUser")
    public void deleteUserPass() throws DataAccessException {
        User user = new User("testUser", "password", "test@example.com");
        userDAO.insertUser(user);
        userDAO.deleteUser(user.getUsername());
        Assertions.assertNull(userDAO.getUser(user.getUsername()));
    }

    // Negative Test for deleteUser (user doesn't exist)
    @Test
    @Order(16)
    @DisplayName("Negative: deleteUser")
    public void deleteUserFail() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.deleteUser("nonExistingUser");
        });
    }


    ///   GameDAO   ///

    // Positive Test for insertGame
    @Test
    @Order(17)
    @DisplayName("Positive: insertGame")
    public void insertGamePass() throws DataAccessException {
        Integer gameID = 1; // This should be a unique ID, probably obtained from the database or some ID generation strategy
        String gameName = "testGame";

        // Create a new Game object with the game ID and name
        Game game = new Game(gameID, gameName);

        // Set the chess game
        game.setGame(chessGame);

        // Insert the game into the database
        gameDAO.insertGame(game);

        // Retrieve the game from the database and check if it's not null
        Assertions.assertNotNull(gameDAO.findGameById(game.getGameID()));
    }


    // Negative Test for insertGame (attempting to insert a game with an existing ID)
    @Test
    @Order(18)
    @DisplayName("Negative: insertGame")
    public void insertGameFail() throws DataAccessException {
        Integer gameID = 1; // This should be a unique ID, probably obtained from the database or some ID generation strategy
        String gameName = "testGame";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(gameID, gameName); // Using the correct constructor
        game.setGame(chessGame); // Setting the chess game
        gameDAO.insertGame(game);
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.insertGame(game); // Attempting to insert the same game again
        });
    }

    // Positive Test for findGameById
    @Test
    @Order(19)
    @DisplayName("Positive: findGame")
    public void findGameByIdPass() throws DataAccessException {
        Integer gameID = 2; // This should be a unique ID
        String gameName = "testGame";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(gameID, gameName); // Using the correct constructor
        game.setGame(chessGame); // Setting the chess game
        gameDAO.insertGame(game);
        Game foundGame = gameDAO.findGameById(game.getGameID());
        Assertions.assertEquals(game.getGameID(), foundGame.getGameID());
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
        Integer gameID1 = 3; // Unique ID
        Integer gameID2 = 4; // Unique ID
        String gameName1 = "testGame1";
        String gameName2 = "testGame2";
        ChessGame chessGame1 = new ChessGameImpl(); // Creating an instance of ChessGame
        ChessGame chessGame2 = new ChessGameImpl(); // Creating another instance of ChessGame
        Game game1 = new Game(gameID1, gameName1); // Using the correct constructor
        Game game2 = new Game(gameID2, gameName2); // Using the correct constructor
        game1.setGame(chessGame1); // Setting the chess game
        game2.setGame(chessGame2); // Setting the chess game
        gameDAO.insertGame(game1);
        gameDAO.insertGame(game2);
        List<Game> games = gameDAO.findAllGames();
        Assertions.assertTrue(games.size() >= 2); // Check that at least 2 games are found
    }

    // Positive Test for claimSpot
    @Test
    @Order(22)
    @DisplayName("Positive: claimSpot")
    public void claimSpotPass() throws DataAccessException {
        Integer gameID = 5; // Unique ID
        String gameName = "testGame";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(gameID, gameName); // Using the correct constructor
        game.setGame(chessGame); // Setting the chess game
        gameDAO.insertGame(game);
        gameDAO.claimSpot(game.getGameID(), "testUser", ChessGame.TeamColor.WHITE);
        Game updatedGame = gameDAO.findGameById(game.getGameID());
        Assertions.assertEquals("testUser", updatedGame.getWhiteUsername());
    }

    // Negative Test for claimSpot (spot already claimed)
    @Test
    @Order(23)
    @DisplayName("Negative: claimSpot")
    public void claimSpotFail() throws DataAccessException {
        Integer gameID = 6; // Unique ID
        String gameName = "testGame";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(gameID, gameName); // Using the correct constructor
        game.setGame(chessGame); // Setting the chess game
        game.setWhiteUsername("testUser");
        gameDAO.insertGame(game);
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.claimSpot(game.getGameID(), "anotherUser", ChessGame.TeamColor.WHITE);
        });
    }

    // Positive Test for updateGame
    @Test
    @Order(24)
    @DisplayName("Positive: updateGame")
    public void updateGamePass() throws DataAccessException {
        Integer gameID = 7; // Unique ID
        String gameName = "testGame";
        ChessGame chessGame = new ChessGameImpl(); // Creating an instance of ChessGame
        Game game = new Game(gameID, gameName); // Using the correct constructor
        game.setGame(chessGame); // Setting the chess game
        gameDAO.insertGame(game);
        ChessGame newChessGame = new ChessGameImpl(); // Assuming a new state is created
        gameDAO.updateGame(game.getGameID(), newChessGame);
        Game updatedGame = gameDAO.findGameById(game.getGameID());
        // Assuming there's a way to validate the updated state, perhaps via a toString or equivalent
        Assertions.assertEquals(newChessGame.toString(), updatedGame.getGame().toString());
    }

    // Negative Test for updateGame (game does not exist)
    @Test
    @Order(25)
    @DisplayName("Negative: updateGame")
    public void updateGameFail() throws DataAccessException {
        ChessGame newChessGame = new ChessGameImpl(); // Correct instantiation
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(9999, newChessGame); // Assuming ID 9999 does not exist
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
