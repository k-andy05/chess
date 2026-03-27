package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        try {
            var map = new Gson().fromJson(json, HashMap.class);

            // Defensively parse the status (default to ServerError if missing)
            Code status = Code.ServerError;
            if (map != null && map.containsKey("status") && map.get("status") != null) {
                try {
                    status = Code.valueOf(map.get("status").toString());
                } catch (IllegalArgumentException e) {
                    // If the server sends a status that isn't ServerError or ClientError, it defaults to ServerError
                }
            }

            // Defensively parse the message
            String message = "Unknown error";
            if (map != null && map.containsKey("message") && map.get("message") != null) {
                message = map.get("message").toString();
            }

            return new ResponseException(status, message);

        } catch (Exception e) {
            // Fallback just in case the body isn't valid JSON at all
            return new ResponseException(Code.ServerError, json);
        }
    }
}
