package dataaccess;

import model.UserData;
import java.util.HashMap;

public class UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public void clearAllUser() {
        users.clear();
    }

    public void createUser(String username, String password, String email) {
        users.put(username, new UserData(username, password, email));
    }

    public UserData getUser(String username) {
        return users.get(username);
    }
}
