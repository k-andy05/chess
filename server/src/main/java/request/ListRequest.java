package request;

import io.javalin.http.Context;

public class ListRequest {
    public String authToken;

    public ListRequest (Context ctx) {
        this.authToken = ctx.header("authorization");
        if (this.authToken != null && this.authToken.endsWith("\"")) {
            this.authToken = this.authToken.substring(0, this.authToken.length() - 1);
        }
    }
}
