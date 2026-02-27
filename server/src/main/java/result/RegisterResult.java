package result;

public class RegisterResult {
    private String authToken;
    private String username;

    public RegisterResult(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String toJson() {
        return "{\"username\":\"" + username + "\",\"authToken\"" + authToken + "\"}";
    }
}
