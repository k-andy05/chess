package server;

import exception.InvalidRequestException;
import exception.UserNotFoundException;
import handler.*;
import io.javalin.*;
import com.google.gson.Gson;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Handler handler = new Handler();
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // Clear endpoint
        javalin.delete("/db", ctx -> { // If user sends a delete http request to the /db path, then take the Context object from the message which you can then use
            handler.clear(ctx);
        });

        // Register endpoint
        javalin.post("/user", ctx -> {
            handler.register(ctx);
            ctx.status(200);
        });

        // Login endpoint
        javalin.post("/session", ctx -> {
           handler.login(ctx);
        });

        // Logout endpoint
        javalin.delete("/session", ctx -> {
           handler.logout(ctx);
        });

        // List endpoint
        javalin.get("/game", ctx -> {
            handler.list(ctx);
        });

        // Create Game endpoint
        javalin.post("/game", ctx -> {
            handler.create(ctx);
        });

        // Join Game endpoint
        javalin.put("/game", ctx -> {
            handler.join(ctx);
        });

        javalin.exception(InvalidRequestException.class, (e, ctx) -> {
            ctx.status(e.getStatusCode());
            String json = gson.toJson(Map.of("message", e.getMessage()));
//            ctx.json(Map.of("message", e.getMessage()));
            ctx.result(json);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
