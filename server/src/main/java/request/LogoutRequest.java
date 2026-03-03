package request;

import io.javalin.http.Context;

public class LogoutRequest {
    public String authToken;

    public LogoutRequest (String authToken) {
        this.authToken = authToken;
    }
}
