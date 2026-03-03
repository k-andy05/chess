package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class AuthDAO {

    private static final HashMap<String, AuthData> sessions = new HashMap<>();

    public void clearAllAuth() {
        sessions.clear();
    }

    public void createAuth(String authToken, String username) {
        sessions.put(authToken, new AuthData(authToken, username));
    }

    public static AuthData getAuth(String authToken) {
        return sessions.get(authToken);
    }

    public void deleteAuth(String authToken) {
        sessions.remove(authToken);
    }
}
