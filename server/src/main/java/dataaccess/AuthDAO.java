package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;

public class AuthDAO {

    private static final HashMap<String, AuthData> sessions = new HashMap<>();

    public static void clearAllAuth() {
        sessions.clear();
    }

    public static void createAuth(String authToken, String username) {
        sessions.put(authToken, new AuthData(authToken, username));
    }

    public static AuthData getAuth(String authToken) {
        return new AuthData("test_auth_token", "user3");
    }

    public static void deleteAuth(String authToken) {}
}
