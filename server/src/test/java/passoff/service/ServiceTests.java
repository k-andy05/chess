package passoff.service;

import model.*;
import org.junit.jupiter.api.*;
import service.Service;
import dataaccess.*;
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
        service.register(new RegisterRequest(registerBody));
        LoginResult loginResult = service.login(new LoginRequest(loginBody));
        service.create(new CreateRequest(game1, loginResult.authToken));
        service.create(new CreateRequest(game2, loginResult.authToken));

        ListResult result = service.list(new ListRequest(loginResult.authToken));
        assertNotNull(result);
        assertEquals(2, result.gameList.size());
    }

    @Test
    public void listInvalidToken() throws Exception {
        service.register(new RegisterRequest(registerBody));
        LoginResult loginResult = service.login(new LoginRequest(loginBody));
        service.create(new CreateRequest(game1, loginResult.authToken));
        service.create(new CreateRequest(game2, loginResult.authToken));

        String differentAuthToken = UUID.randomUUID().toString();
        assertThrows(InvalidRequestException.class, () ->
                service.list(new ListRequest(differentAuthToken))
        );
    }


}
