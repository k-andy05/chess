package handler;

import io.javalin.http.Context;
import request.RegisterRequest;
import service.*;
import response.*;
import request.*;

// This class will take json from server, direct it to correct service class, and format args as a Request class object
// it will also receive different response objects and pass on that data to the server
public class Handler {

    private final Service service = new Service();

    public void clear(Context ctx) {
//        Service service = new Service();
        ClearResponse response = service.clear();

        ctx.status(200);
        ctx.json(response);
    }

    public void register(Context ctx) {
        // TODO add exception handling later to allow for 200/403/400/500 errors
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);

//        Service service = new Service();
        RegisterResponse response = service.register(request);

        ctx.status(200);
        ctx.json(response);
    }

    public void login(Context ctx) {
        LoginRequest request = ctx.bodyAsClass(LoginRequest.class);

//        Service service = new Service();
        LoginResponse response = service.login(request);

        ctx.status(200);
        ctx.json(response);
    }

    public void logout(Context ctx) {
        LogoutRequest request = ctx.bodyAsClass(LogoutRequest.class);

//        Service service = new Service();
        LogoutResponse response = service.logout(request);

        ctx.status(200);
        ctx.json(response);
    }

    public void list(Context ctx) {
        ListRequest request = ctx.bodyAsClass(ListRequest.class);

//        Service service = new Service();
        ListResponse response = service.list(request);

        ctx.status(200);
        ctx.json(response);
    }

    public void create(Context ctx) {
        CreateRequest request = ctx.bodyAsClass(CreateRequest.class);

//        Service service = new Service();
        CreateResponse response = service.create(request);

        ctx.status(200);
        ctx.json(response);
    }

    public void join(Context ctx) {
        JoinRequest request = ctx.bodyAsClass(JoinRequest.class);

//        Service service = new Service();
        JoinResponse response = service.join(request);

        ctx.status(200);
        ctx.json(response);
    }
}
