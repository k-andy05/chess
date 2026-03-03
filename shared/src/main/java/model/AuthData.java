package model;

public class AuthData {
    public String authToken;
    public String username;

    public AuthData (String authToken, String username) {
        this.authToken = authToken;
        if (this.authToken != null && this.authToken.endsWith("\"")) {
            this.authToken = this.authToken.substring(0, this.authToken.length() - 1);
        }
        this.username = username;
    }
}
