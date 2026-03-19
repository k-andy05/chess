package dataaccess;
// Use same database, ask
import exception.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlDAOTests {

    private MySqlUserDAO userDAO;
    private MySqlAuthDAO authDAO;
    private MySqlGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws Exception {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
        authDAO.clearAllAuth();
        gameDAO.clearAllGame();
        userDAO.clearAllUser();
    }

    @Test
    @DisplayName("ClearUserTable - Success")
    @Order(12)
    public void clearUserSuccess() throws Exception {
        userDAO.createUser("testUser", "password", "test@email.com");
        userDAO.clearAllUser();
        UserData retrievedUser = userDAO.getUser("testUser");
        assertNull(retrievedUser, "Database should be empty after clearing userdata table");
    }

    @Test
    @DisplayName("CreateUser - Success")
    @Order(2)
    public void createUserSuccess() throws Exception {
        userDAO.createUser("testUser", "password", "test@email.com");
        assertNotNull(userDAO.getUser("testUser"), "User was not inserted into userdata table");
    }

    @Test
    @DisplayName("CreateUser - Username Exists")
    @Order(1)
    public void createUserFail() throws Exception {
        userDAO.createUser("testUser", "password", "test@email.com");
        assertThrows(DataAccessException.class, () ->
                userDAO.createUser("testUser", "diffPassword", "new@email.com")
        );
    }

    @Test
    @DisplayName("GetUser - Success")
    @Order(3)
    public void getUserSuccess() throws Exception {
        userDAO.createUser("testUser", "password", "test@email.com");
        UserData retrievedUser = userDAO.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username, "Username did not match");
        assertTrue(BCrypt.checkpw("password", retrievedUser.password), "Password did not match");
        assertEquals("test@email.com", retrievedUser.email, "Username did not match");
    }

    @Test
    @DisplayName("GetUser - Wrong Username")
    @Order(4)
    public void getUserFail() throws Exception {
        userDAO.createUser("testUser", "password", "test@email.com");
        UserData retrievedUser = userDAO.getUser("testUserWrong");
        assertNull(retrievedUser, "You managed to create a user with magic");
    }

    @Test
    @DisplayName("GetUserByEmail - Successs")
    @Order(5)
    public void getUserByEmailSuccess() throws Exception {
        userDAO.createUser("testUser", "password", "test@email.com");
        UserData retrievedUser = userDAO.getUserByEmail("test@email.com");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username, "Username did not match");
        assertTrue(BCrypt.checkpw("password", retrievedUser.password), "Password did not match");
        assertEquals("test@email.com", retrievedUser.email, "Username did not match");
    }

    @Test
    @DisplayName("GetUserByEmail - Fail")
    @Order(6)
    public void getUserByEmailFail() throws Exception {
        userDAO.createUser("testUser", "password", "test@email.com");
        UserData retrievedUser = userDAO.getUserByEmail("testWRONG@email.com");
        assertNull(retrievedUser, "Email may be registered under a different user by accident");
    }

    @Test
    @DisplayName("ClearAllGame - Successs")
    public void clearAllGame() throws Exception {
        gameDAO.createGame("testGameName");
        gameDAO.clearAllGame();
        var games = gameDAO.getGames();
        assertTrue(games.isEmpty());
    }

    @Test
    @DisplayName("GetGameList - Successs")
    public void getGameListSuccess() throws Exception {
        gameDAO.createGame("testGameName1");
        gameDAO.createGame("testGameName2");
        var games = gameDAO.getGames();
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("GetGameList - Empty Table")
    public void getGameListFail() throws Exception {
        var games = gameDAO.getGames();
        assertTrue(games.isEmpty());
    }

    @Test
    @DisplayName("UserJoin - Successs")
    @Order(10)
    public void userJoinSuccess() throws Exception {
        gameDAO.createGame("testGameName");
        int gameID = gameDAO.getGameByName("testGameName").gameID;
        gameDAO.userJoin("WHITE", gameID, "testUser");
        GameData game = gameDAO.getGameByName("testGameName");
        assertEquals("testUser", game.whiteUsername, "User was not able to join game");
    }

    @Test
    @DisplayName("GetGameByName - Successs")
    public void getGameByNameSuccess() throws Exception {
        gameDAO.createGame("testGameName");
        GameData game = gameDAO.getGameByName("testGameName");
        assertNotNull(game);
        assertEquals("testGameName", game.gameName);
    }

    @Test
    @DisplayName("GetGameByName - Fail")
    public void getGameByNameFail() throws Exception {
        gameDAO.createGame("testGameName");
        GameData game = gameDAO.getGameByName("testGameNameWRONG");
        assertNull(game);
    }

    @Test
    @DisplayName("GetGameByID - Successs")
    public void getGameByIDSuccess() throws Exception {
        gameDAO.createGame("testGameName");
        int gameID = gameDAO.getGameByName("testGameName").gameID;
        GameData game = gameDAO.getGameByID(gameID);
        assertNotNull(game);
        assertEquals("testGameName", game.gameName);
    }

    @Test
    @DisplayName("GetGameByID - Fail")
    public void getGameByIDFail() throws Exception {
        gameDAO.createGame("testGameName");
        GameData game = gameDAO.getGameByID(12345);
        assertNull(game);
    }

    @Test
    @DisplayName("CreateGame - Successs")
    @Order(8)
    public void createGameSuccess() throws Exception {
        gameDAO.createGame("testGameName");
        assertNotNull(gameDAO.getGameByName("testGameName"));
    }

    @Test
    @DisplayName("ClearAllAuth - Successs")
    public void clearAllAuthSuccess() throws Exception {
    }

    @Test
    @DisplayName("CreateAuth - Successs")
    public void createAuthSuccess() throws Exception {
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, "testUser");
        AuthData retrievedAuth = authDAO.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals("testUser", retrievedAuth.username, "Username doesn't match");
        assertEquals(authToken, retrievedAuth.authToken, "AuthToken doesn't match");
    }

    @Test
    @DisplayName("CreateAuth - Fail")
    public void createAuthFail() throws Exception {
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, "testUser");
        assertThrows(DataAccessException.class, () ->
                authDAO.createAuth(authToken, "WRONGUser"),
                "Same authtoken allowed for another user session creation."
                );
    }

    @Test
    @DisplayName("GetAuth - Successs")
    public void getAuthSuccess() throws Exception {
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, "testUser");
        AuthData retrievedAuth = authDAO.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(authToken, retrievedAuth.authToken);
        assertEquals("testUser", retrievedAuth.username);
    }

    @Test
    @DisplayName("GetAuth - Fail")
    public void getAuthFail() throws Exception {
        String authToken = UUID.randomUUID().toString();
        String newAuthToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, "testUser");
        AuthData retrievedAuth = authDAO.getAuth(newAuthToken);
        assertNull(retrievedAuth);
    }

    @Test
    @DisplayName("DeleteAuth - Successs")
    public void deleteAuthSuccess() throws Exception {
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, "testUser");
        authDAO.deleteAuth(authToken);
        AuthData retrievedAuth = authDAO.getAuth(authToken);
        assertNull(retrievedAuth);
    }

    @Test
    @DisplayName("DeleteAuth - Fail")
    public void deleteAuthFail() throws Exception {
        String authToken = UUID.randomUUID().toString();
        String newAuthToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, "testUser");
        authDAO.deleteAuth(newAuthToken);
        AuthData retrievedAuth = authDAO.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(authToken, retrievedAuth.authToken);
        assertEquals("testUser", retrievedAuth.username);
    }
}
