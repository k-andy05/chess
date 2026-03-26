package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void registerSuccess() throws ResponseException {
        var authData = facade.register(new RegisterRequest("{\"username\":\"testuser\",\"password\":\"123\",\"email\":\"test@email.com\"}"));
        assertTrue(authData.authToken.length() > 10);
        System.out.println("Authdata = " + authData);
        assertEquals("testuser", authData.username);
    }

    @Test
    public void registerFail() throws ResponseException {

    }

}
