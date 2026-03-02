package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;

public class AuthDAO {

    private  final HashMap<String, AuthData> sessions = new HashMap<>();

    public void clearAllAuth() {
        sessions.clear();
    }

    public void createAuth(String authToken, String username) {
        sessions.put(authToken, new AuthData(authToken, username));
    }

    public AuthData getAuth(String authToken) {
        return sessions.get(authToken);
    }

    public AuthData getAuthByUsername(String username) {
        for (AuthData session : sessions.values()) {
            if (session.username.equals(username)) {
                return session;
            }
        }
        return null;
    }

    public void deleteUserSessions(String username) {
        for (AuthData session : sessions.values()) {
            if (session.username.equals(username)) {
                sessions.remove(session);
            }
        }
    }

    public void deleteAuth(String authToken) {
        sessions.remove(authToken);
    }
}
