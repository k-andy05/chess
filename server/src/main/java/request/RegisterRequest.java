package request;

public class RegisterRequest {
    public String username;
    public String password;
    public String email;

    public RegisterRequest (String body) {
        this.username = extract(body, "username");
        this.password = extract(body, "password");
        this.email = extract(body, "email");
    }

    private String extract (String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) {return null;}
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
