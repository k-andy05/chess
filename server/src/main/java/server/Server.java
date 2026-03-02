package server;

import exception.UserNotFoundException;
import handler.*;
import io.javalin.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // Clear endpoint
        javalin.delete("/db", ctx -> { // If user sends a delete http request to the /db path, then take the Context object from the message which you can then use
            new Handler().clear(ctx);
        });

        // Register endpoint
        javalin.post("/user", ctx -> {
            new Handler().register(ctx);
            ctx.status(200);
        });

        // Login endpoint
        javalin.post("/session", ctx -> {
           new Handler().login(ctx);
        });

        // Logout endpoint
        javalin.delete("/session", ctx -> {
           new Handler().logout(ctx);
        });

        // List endpoint
        javalin.get("/game", ctx -> {
            new Handler().list(ctx);
        });

        // Create Game endpoint
        javalin.post("/game", ctx -> {
            new Handler().create(ctx);
        });

        // Join Game endpoint
        javalin.put("/game", ctx -> {
            new Handler().join(ctx);
        });

        javalin.exception(UserNotFoundException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.json(Map.of("message", e.getMessage()));
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
