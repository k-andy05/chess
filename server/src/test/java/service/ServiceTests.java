package service;

import org.junit.jupiter.api.*;
import service.Service;
import request.*;
import result.*;
import exception.InvalidRequestException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private Service service;
    private final String registerBody = "{\"username\":\"NewUser\",\"password\":\"abc123\",\"email\":\"example@email.com\"}";
    private final String loginBody = "{\"{\"username\":\"NewUser\",\"password\":\"abc123\"}";
    private final String game1 = "{\"gameName\":\"testGame1\"}";
    private final String game2 = "{\"gameName\":\"testGame2\"}";
    private final String joinBody = "{\"playerColor\":\"WHITE\",\"gameID\":1}";
//    private final String gameList = ""

    @BeforeEach
    public void setup() {
        service = new Service();
        service.clear();
    }

    // Register
    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(registerBody);
        RegisterResult result = service.register(request);

        assertNotNull(result);
        assertEquals("NewUser", result.username);
        assertNotNull(result.authToken);
    }

    @Test
    public void registerDuplicateEmail() throws Exception {
        service.register(new RegisterRequest(registerBody));
        String duplicateEmail = "{\"username\":\"NewUser2\",\"password\":\"def456\",\"email\":\"example@email.com\"}";

        assertThrows(InvalidRequestException.class, ()  ->
                service.register(new RegisterRequest(duplicateEmail))
        );
    }

    // Login
    @Test
    public void loginSuccess() throws Exception {
        service.register(new RegisterRequest(registerBody));
        LoginRequest request = new LoginRequest(loginBody);
        LoginResult result = service.login(request);

        assertNotNull(result);
        assertEquals("NewUser", result.username);
        assertNotNull(result.authToken);
    }

    @Test
    public void loginUsernameDoesNotExist() throws Exception {
        service.register(new RegisterRequest(registerBody));
        String loginBody = "{\"{\"username\":\"UnregisteredUser\",\"password\":\"abc123\"}";

        assertThrows(InvalidRequestException.class, () ->
                service.login(new LoginRequest(loginBody))
        );
    }

    // Logout
    @Test
    public void logoutSuccess() throws Exception {
        service.register(new RegisterRequest(registerBody));
        LoginRequest loginRequest = new LoginRequest(loginBody);
        LoginResult loginResult = service.login(loginRequest);

        LogoutResult result = service.logout(new LogoutRequest(loginResult.authToken));
        assertNotNull(result);
    }

    @Test
    public void logoutWrongAuthToken() throws Exception {
        service.register(new RegisterRequest(registerBody));
        service.login(new LoginRequest(loginBody));

        String differentAuthToken = UUID.randomUUID().toString();
        assertThrows(InvalidRequestException.class, () ->
                service.logout(new LogoutRequest(differentAuthToken))
        );
    }

    // List
    @Test
    public void listSuccess() throws Exception {
        String authToken = initialSetup();
        service.create(new CreateRequest(game2, authToken));
        ListResult result = service.list(new ListRequest(authToken));
        assertNotNull(result);
        assertEquals(2, result.gameList.size());
    }

    @Test
    public void listInvalidToken() throws Exception {
        String authToken = initialSetup();
        service.create(new CreateRequest(game2, authToken));

        String differentAuthToken = UUID.randomUUID().toString();
        assertThrows(InvalidRequestException.class, () ->
                service.list(new ListRequest(differentAuthToken))
        );
    }

    // Create
    @Test
    public void createSuccess() throws Exception {
        service.register(new RegisterRequest(registerBody));
        LoginResult loginResult = service.login(new LoginRequest(loginBody));

        CreateResult result = service.create(new CreateRequest(game1, loginResult.authToken));
        assertNotNull(result);
        assertEquals(1, result.gameID);
    }

    @Test
    public void createDuplicateGameNames() throws Exception {
        String authToken = initialSetup();
        assertThrows(InvalidRequestException.class, () ->
                service.create(new CreateRequest(game1, authToken))
        );
    }

    // Clear
    @Test
    public void clearSuccess() throws Exception {
        initialSetup();
        ClearResult result = service.clear();
        assertNotNull(result);
        assertEquals("{}", result.toJson());
        assertThrows(InvalidRequestException.class, () ->
                service.login(new LoginRequest(loginBody))
        );
    }

    // Join
    @Test
    public void joinSuccess() throws Exception {
        String authToken = initialSetup();
        JoinResult result = service.join(new JoinRequest(authToken, joinBody));
        assertNotNull(result);
        assertEquals("{}", result.toJson());
    }

    @Test
    public void joinColorTaken() throws Exception {
        String authToken = initialSetup();
        String registerBody2 = "{\"username\":\"NewUser2\",\"password\":\"123abc\",\"email\":\"new@email.com\"}";
        String loginBody2 = "{\"{\"username\":\"NewUser\",\"password\":\"abc123\"}";
        service.join(new JoinRequest(authToken, joinBody));
        service.register(new RegisterRequest(registerBody2));
        LoginResult loginResult2 = service.login(new LoginRequest(loginBody2));

        String joinBody2 = "{\"playerColor\":\"WHITE\",\"gameID\":1}";
        assertThrows(InvalidRequestException.class, () ->
                service.join(new JoinRequest(loginResult2.authToken, joinBody2))
        );
    }

    private String initialSetup() {
        service.register(new RegisterRequest(registerBody));
        LoginResult loginResult = service.login(new LoginRequest(loginBody));
        service.create(new CreateRequest(game1, loginResult.authToken));
        return loginResult.authToken;
    }
}
