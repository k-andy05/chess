package request;

import io.javalin.http.Context;

public class LogoutRequest {
    public String authToken;

    public LogoutRequest (Context ctx) {
        this.authToken = ctx.header("authorization");
    }
}
