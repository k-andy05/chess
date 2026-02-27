package dataaccess;

import model.UserData;
import java.util.HashMap;

public class UserDAO {
    private static final HashMap<String, UserData> users = new HashMap<>();

    public static void clearAllUser() {
        users.clear();
    }

    public static void createUser(String username, String password, String email) {
        users.put(username, new UserData(username, password, email));
    }

    public static UserData getUser(String username) {
        return users.get(username);
    }
}
