package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws ResponseException {
        var authData = facade.register(new RegisterRequest("{\"username\":\"testuser\",\"password\":\"123\",\"email\":\"test@email.com\"}"));

        assertTrue(authData.authToken.length() > 10);
        System.out.println("Authdata = " + authData);
        assertEquals("testuser", authData.username);

        String loginBody = "{\"username\":\"testuser\",\"password\":\"123\"}";
        var loginResult = facade.login(new LoginRequest(loginBody));

        Assertions.assertNotNull(loginResult.authToken);
        assertEquals("testuser", loginResult.username);
    }

    @Test
    public void registerFail() throws ResponseException {
        facade.register(new RegisterRequest("{\"username\":\"testuser\",\"password\":\"123\",\"email\":\"test@email.com\"}"));
        assertThrows(ResponseException.class, () ->
                facade.register(new RegisterRequest("{\"username\":\"testuser\",\"password\":\"abc\",\"email\":\"different@email.com\"}"))
        );
    }

    @Test
    public void loginSuccess() throws ResponseException {
        registerSetup();
        var loginResult = facade.login(new LoginRequest("{\"username\":\"testuser\",\"password\":\"123\"}"));
        assertEquals("testuser", loginResult.username);
    }

    @Test
    public void loginFail() throws ResponseException {
        registerSetup();
        assertThrows(ResponseException.class, () ->
                facade.login(new LoginRequest("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
        );

    }

    @Test
    public void logoutSuccess() throws ResponseException {
        RegisterResult registerResult = registerSetup();
        facade.logout(new LogoutRequest(registerResult.authToken));
        assertThrows(ResponseException.class, () ->
                facade.logout(new LogoutRequest(registerResult.authToken))
        );
    }

    @Test
    public void logoutFail() throws ResponseException {
        assertThrows(ResponseException.class, () ->
                facade.logout(new LogoutRequest("asdhawpeoivunahjsdjf238952"))
        );
    }

    @Test
    public void createSuccess() throws ResponseException {
        var registerResult = registerSetup();
        var createResult = facade.create(new CreateRequest("{\"gameName\":\"testgame\"}", registerResult.authToken));
        assertNotNull(createResult.gameID);
        String joinBody = String.format("{\"playerColor\":\"WHITE\",\"gameID\":%d}", createResult.gameID);
        facade.join(new JoinRequest(registerResult.authToken, joinBody));
    }

    @Test
    public void createFail() throws ResponseException {
        var registerResult = registerSetup();
        facade.create(new CreateRequest("{\"gameName\":\"testgame\"}", registerResult.authToken));
        assertThrows(ResponseException.class, () ->
                facade.create(new CreateRequest("{\"gameName\":\"testgame\"}", registerResult.authToken))
        );
    }

    @Test
    public void listSuccess() throws ResponseException {
        var registerResult = registerSetup();
        var listResult = facade.list(new ListRequest(registerResult.authToken));
        assertEquals(0, listResult.gameList.size());
        var createResult = facade.create(new CreateRequest("{\"gameName\":\"testgame\"}", registerResult.authToken));
        listResult = facade.list(new ListRequest(registerResult.authToken));
        assertEquals(1, listResult.gameList.size());
        assertEquals("testgame", listResult.gameList.getFirst().gameName);
        assertEquals(createResult.gameID, listResult.gameList.getFirst().gameID);
        assertNull(listResult.gameList.getFirst().whiteUsername);
        assertNull(listResult.gameList.getFirst().blackUsername);
    }

    @Test
    public void listFail() throws ResponseException {
        assertThrows(ResponseException.class, () ->
                facade.list(new ListRequest("bad-auth-token"))
        );
    }

    @Test
    public void joinSuccess() throws ResponseException {
        var registerResult = registerSetup();
        var createResult = facade.create(new CreateRequest("{\"gameName\":\"testgame\"}", registerResult.authToken));
        String joinBody = String.format("{\"playerColor\":\"WHITE\",\"gameID\":%d}", createResult.gameID);
        facade.join(new JoinRequest(registerResult.authToken, joinBody));
        var listResult = facade.list(new ListRequest(registerResult.authToken));
        assertEquals(createResult.gameID, listResult.gameList.getFirst().gameID);
        assertEquals("testgame", listResult.gameList.getFirst().gameName);
        assertEquals("testuser", listResult.gameList.getFirst().whiteUsername);
        assertNull(listResult.gameList.getFirst().blackUsername);
    }

    @Test
    public void joinFail() throws ResponseException {
        var registerResult = registerSetup();
        var createResult = facade.create(new CreateRequest("{\"gameName\":\"testgame\"}", registerResult.authToken));
        String joinBody = String.format("{\"playerColor\":\"WHITE\",\"gameID\":%d}", createResult.gameID);
        facade.join(new JoinRequest(registerResult.authToken, joinBody));
        String requestBody = "{\"username\":\"testuser2\",\"password\":\"abc\",\"email\":\"different@email.com\"}";
        var registerResult2 = facade.register(new RegisterRequest(requestBody));
        String joinBody2 = String.format("{\"playerColor\":\"WHITE\",\"gameID\":%d}", createResult.gameID);
        assertThrows(ResponseException.class, () ->
                facade.join(new JoinRequest(registerResult2.authToken, joinBody2))
        );
    }

    @Test
    public void observeSuccess() throws ResponseException {
        var registerResult = registerSetup();
        var createResult = facade.create(new CreateRequest("{\"gameName\":\"testgame\"}", registerResult.authToken));
        String requestBody = "{\"username\":\"testuser2\",\"password\":\"abc\",\"email\":\"different@email.com\"}";
        var registerResult2 = facade.register(new RegisterRequest(requestBody));
        String joinBody2 = String.format("{\"playerColor\":\"WHITE\",\"gameID\":%d}", createResult.gameID);
        facade.observe(new JoinRequest(registerResult2.authToken, joinBody2));
    }

    @Test
    public void observeFail() throws ResponseException {
        var registerResult = registerSetup();
        var createResult = facade.create(new CreateRequest("{\"gameName\":\"testgame\"}", registerResult.authToken));
        String requestBody = "{\"username\":\"testuser2\",\"password\":\"abc\",\"email\":\"different@email.com\"}";
        var registerResult2 = facade.register(new RegisterRequest(requestBody));
        String joinBody2 = String.format("{\"playerColor\":\"WHITE\",\"gameID\":%d}", 1000);
        assertThrows(ResponseException.class, () ->
                facade.observe(new JoinRequest(registerResult2.authToken, joinBody2))
        );
    }

    @Test
    public void clearSuccess() throws ResponseException {
        registerSetup();
        facade.clear();
        assertThrows(ResponseException.class, () ->
                facade.login(new LoginRequest("{\"username\":\"testuser\",\"password\":\"123\"}")));
    }

    public RegisterResult registerSetup() throws ResponseException {
        return facade.register(new RegisterRequest("{\"username\":\"testuser\",\"password\":\"123\",\"email\":\"test@email.com\"}"));
    }

}
