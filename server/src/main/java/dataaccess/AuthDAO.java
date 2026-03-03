package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class AuthDAO {

    private static final HashMap<String, AuthData> SESSIONS = new HashMap<>();

    public void clearAllAuth() {
        SESSIONS.clear();
    }

    public void createAuth(String authToken, String username) {
        SESSIONS.put(authToken, new AuthData(authToken, username));
    }

    public static AuthData getAuth(String authToken) {
        return SESSIONS.get(authToken);
    }

    public void deleteAuth(String authToken) {
        SESSIONS.remove(authToken);
    }
}
