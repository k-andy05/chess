package request;

public class CreateRequest {
    public String authToken;
    public String gameName;

    public CreateRequest(String body, String authToken) {
        this.authToken = authToken;
        this.gameName = RequestHelper.extract(body);
    }
}
