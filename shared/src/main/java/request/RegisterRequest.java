package request;

import result.RegisterResult;

public class RegisterRequest {
    public String username;
    public String password;
    public String email;

    public RegisterRequest (String body) {
        this.username = RequestHelper.extractWith(body, "username");
        this.password = RequestHelper.extractWith(body, "password");
        this.email = RequestHelper.extractWith(body, "email");
    }
}
