package result;

public class LoginResult {
    public String username;
    public String authToken;

    public LoginResult (String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String toJson() {
        return "{\"username\":\"" + username + "\",\"authToken\":" + authToken + "\"}";
    }
}
