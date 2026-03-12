package dataaccess;

import exception.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySqlUserDAOTests {

    private MySqlUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        userDAO.clearAllUser();
    }

    @Test
    void testCreateUserSuccess() throws DataAccessException {
//        User user = new User("player1", "pas")
    }
}
