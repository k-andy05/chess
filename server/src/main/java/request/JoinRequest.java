package request;

import io.javalin.http.Context;

public class JoinRequest {
    public String authToken;
    public String playerColor; // Change to be custom variable WHITE or BLACK
    public int gameID;

    public JoinRequest (Context ctx) {
        String body = ctx.body();
        this.authToken = ctx.header("authorization");
        this.playerColor = extract(body, "playerColor");
        this.gameID = extractInt(body, "gameID");
    }

    private String extract(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;

        start += pattern.length();
        int end = json.indexOf("\"", start);

        return json.substring(start, end);
    }

    private Integer extractInt(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return null;

        start += pattern.length();

        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }
        return Integer.parseInt(json.substring(start, end));
    }
}
