package handler;

import exception.InvalidRequestException;
import io.javalin.http.Context;
import request.RegisterRequest;
import service.*;
import result.*;
import request.*;

public class Handler {

    private final Service service = new Service();

    public void clear(Context ctx) {
        ClearResult result = service.clear();
        ctx.status(200);
        ctx.json(result.toJson());
    }

    public void register(Context ctx) throws InvalidRequestException {
        RegisterRequest request = new RegisterRequest(ctx.body());
        RegisterResult result = service.register(request);
        ctx.status(200);
        ctx.result(result.toJson());
    }

    public void login(Context ctx) throws InvalidRequestException {
        LoginRequest request = new LoginRequest(ctx.body());
        LoginResult result = service.login(request);
        ctx.status(200);
        ctx.result(result.toJson());
    }

    public void logout(Context ctx) throws InvalidRequestException {
        String authToken = ctx.header("authorization");
        LogoutRequest request = new LogoutRequest(authToken);
        LogoutResult result = service.logout(request);
        ctx.status(200);
        ctx.json(result.toJson());
    }

    public void list(Context ctx) throws InvalidRequestException {
        String authToken = ctx.header("authorization");
        ListRequest request = new ListRequest(authToken);
        ListResult result = service.list(request);
        ctx.status(200);
        ctx.json(result.toJson());
    }

    public void create(Context ctx) throws InvalidRequestException {
        String authToken = ctx.header("authorization");
        CreateRequest request = new CreateRequest(ctx.body(), authToken);
        CreateResult result = service.create(request);
        ctx.status(200);
        ctx.result(result.toJson());
    }

    public void join(Context ctx) throws InvalidRequestException {
        String authToken = ctx.header("authorization");
        JoinRequest request = new JoinRequest(authToken, ctx.body());
        JoinResult result = service.join(request);
        ctx.status(200);
        ctx.json(result.toJson());
    }
}
