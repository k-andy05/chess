package request;

public class ListRequest {
    public String authToken;

    public ListRequest (String authToken) {
        this.authToken = authToken;
        if (this.authToken != null && this.authToken.endsWith("\"")) {
            this.authToken = this.authToken.substring(0, this.authToken.length() - 1);
        }
    }
}
