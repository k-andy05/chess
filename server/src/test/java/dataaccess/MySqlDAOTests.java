package dataaccess;
// Use same database, ask
import org.junit.jupiter.api.*;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

public class MySqlDAOTests {

    private static final TestUser TEST_USER = new TestUser("ExistingUser", "existingUserPassword", "eu@mail.com");
    private static TestServerFacade serverFacade;
    private static Server server;
    private static Class<?> databaseManagerClass;

    private MySqlUserDAO userDAO;
    private final String registerBody = "{\"username\":\"NewUser\",\"password\":\"abc123\",\"email\":\"example@email.com\"}";

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @BeforeEach
    public void setUp() { serverFacade.clear(); }

    @AfterAll
    static void stopServer() { server.stop(); }

    @Test
    @DisplayName("ClearUserTable - Success")
    public void clearSuccess() throws Exception {
    }

    @Test
    @DisplayName("CreateUser - Success")
    public void createUserSuccess() throws Exception {
    }

    @Test
    @DisplayName("CreateUser - Fail")
    public void createUserFail() throws Exception {
    }

    @Test
    @DisplayName("GetUser - Success")
    public void getUserSuccess() throws Exception {
    }

    @Test
    @DisplayName("GetUser - Fail")
    public void getUserFail() throws Exception {
    }

    @Test
    @DisplayName("GetUserByEmail - Successs")
    public void getUserByEmailSuccess() throws Exception {
    }

    @Test
    @DisplayName("GetUserByEmail - Fail")
    public void getUserByEmailFail() throws Exception {
    }

    @Test
    @DisplayName("ClearAllGame - Successs")
    public void clearAllGame() throws Exception {
    }

    @Test
    @DisplayName("GetGameList - Successs")
    public void getGameListSuccess() throws Exception {
    }

    @Test
    @DisplayName("GetGameList - Fail")
    public void getGameListFail() throws Exception {
    }
}
