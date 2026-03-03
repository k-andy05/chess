package request;

import io.javalin.http.Context;

public class CreateRequest {
    public String authToken;
    public String gameName;

    public CreateRequest(String body, String authToken) {
        this.authToken = authToken;
        this.gameName = extract(body);
    }

    private String extract(String json) {
        String key = "gameName";
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) {return null;}
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
