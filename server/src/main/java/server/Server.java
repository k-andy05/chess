package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // Clear endpoint
        javalin.delete("/db", ctx -> { // If user sends a delete http request to the /db path, then take the Context object from the message which you can then use
            // TODO call clear service later
            ctx.status(200);
        });

        // Register endpoint
        javalin.post("/user", ctx -> {
            // TODO call service functions later
            ctx.status(200);
        });

        // Login endpoint
        javalin.post("/session", ctx -> {
           // TODO call service functions later
        });

        // Logout endpoint
        javalin.delete("/session", ctx -> {
           // TODO call service functions later
        });

        // List endpoint
        javalin.get("/game", ctx -> {
            // TODO call service functions later
        });

        // Create Game endpoint
        javalin.post("/game", ctx -> {
            // TODO call service functions later
        });

        // Join Game endpoint
        javalin.put("/game", ctx -> {
            // TODO call service functions later
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
