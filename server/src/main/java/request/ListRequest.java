package request;

import io.javalin.http.Context;

public class ListRequest {
    public String authToken;

    public ListRequest (Context ctx) {
        this.authToken = ctx.header("authorization");
    }
}
