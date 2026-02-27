package dataaccess;

import model.AuthData;

public class AuthDAO {

    public static void clearAllAuth() {}

    public static void createAuth(String authToken, String username) {}

    public static AuthData getAuth(String authToken) {
        return new AuthData("test_auth_token", "user3");
    }

    public static void deleteAuth(String authToken) {}
}
