package handler;

import io.javalin.http.Context;
import request.RegisterRequest;
import service.*;
import result.*;
import request.*;

// This class will take json from server, direct it to correct service class, and format args as a Request class object
// it will also receive different result objects and pass on that data to the server
public class Handler {

    private final Service service = new Service();

    public void clear(Context ctx) {
        ClearResult result = service.clear();
        ctx.status(200);
        ctx.json(result.toJson());
    }

    public void register(Context ctx) {
        // TODO add exception handling later to allow for 200/403/400/500 errors
        RegisterRequest request = new RegisterRequest(ctx.body());
        RegisterResult result = service.register(request);

        ctx.status(200);
        ctx.result(result.toJson());
//        ctx.contentType("application/json");
    }

    public void login(Context ctx) {
        LoginRequest request = new LoginRequest(ctx.body());
        LoginResult result = service.login(request);

        ctx.status(200);
        ctx.result(result.toJson());
    }

    public void logout(Context ctx) {
        LogoutRequest request = new LogoutRequest(ctx);
        LogoutResult result = service.logout(request);

        ctx.status(200);
        ctx.json(result.toJson());
    }

    public void list(Context ctx) {
        ListRequest request = new ListRequest(ctx);
        ListResult result = service.list(request);

        ctx.status(200);
        ctx.json(result.toJson());
    }

    public void create(Context ctx) {
        CreateRequest request = new CreateRequest(ctx);
        CreateResult result = service.create(request);

        ctx.status(200);
        ctx.result(result.toJson());
    }

    public void join(Context ctx) {
        JoinRequest request = new JoinRequest(ctx);
        JoinResult result = service.join(request);

        ctx.status(200);
        ctx.json(result.toJson());
    }
}
