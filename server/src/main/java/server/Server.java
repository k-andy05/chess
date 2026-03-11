package server;

import dataaccess.DataAccess;
import dataaccess.DatabaseManager;
import exception.DataAccessException;
import exception.InvalidRequestException;
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

        // Clear endpoint
        javalin.delete("/db", handler::clear);

        // Register endpoint
        javalin.post("/user", handler::register);

        // Login endpoint
        javalin.post("/session", handler::login);

        // Logout endpoint
        javalin.delete("/session", handler::logout);

        // List endpoint
        javalin.get("/game", handler::list);

        // Create Game endpoint
        javalin.post("/game", handler::create);

        // Join Game endpoint
        javalin.put("/game", handler::join);

        // Error Handling
        javalin.exception(InvalidRequestException.class, (e, ctx) -> {
            ctx.status(e.getStatusCode());
            String json = gson.toJson(Map.of("message", e.getMessage()));
            ctx.result(json);
        });
    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
