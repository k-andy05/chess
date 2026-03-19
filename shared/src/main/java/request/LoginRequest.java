package request;

public class LoginRequest {

    public String username;
    public String password;
    public LoginRequest(String body) {
        this.username = RequestHelper.extractWith(body, "username");
        this.password = RequestHelper.extractWith(body, "password");
    }
}
