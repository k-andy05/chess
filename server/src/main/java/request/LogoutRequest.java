package request;

public class LogoutRequest {
    public String authToken;

    public LogoutRequest (String authToken) {
        this.authToken = authToken;
    }
}
