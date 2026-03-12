package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private static final HashMap<String, AuthData> SESSIONS = new HashMap<>();

    @Override
    public void clearAllAuth() {
        SESSIONS.clear();
    }

    @Override
    public void createAuth(String authToken, String username) {
        SESSIONS.put(authToken, new AuthData(authToken, username));
    }

    @Override
    public AuthData getAuth(String authToken) {
        return SESSIONS.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        SESSIONS.remove(authToken);
    }
}
