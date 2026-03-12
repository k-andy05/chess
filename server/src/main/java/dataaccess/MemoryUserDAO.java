package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clearAllUser() {
        users.clear();
    }

    @Override
    public void createUser(String username, String password, String email) {
        users.put(username, new UserData(username, password, email));
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public UserData getUserByEmail(String email) {
        for (UserData user : users.values()) {
            if (user.email.equals(email)) {
                return user;
            }
        }
        return null;
    }
}
