package request;

public class JoinRequest {
    public String authToken;
    public String playerColor; // Change to be custom variable WHITE or BLACK
    public Integer gameID;

    public JoinRequest (String authToken, String body) {
        this.authToken = authToken;
        if (this.authToken != null && this.authToken.endsWith("\"")) {
            this.authToken = this.authToken.substring(0, this.authToken.length() - 1);
        }
        this.playerColor = RequestHelper.extractWith(body, "playerColor");
        this.gameID = RequestHelper.extractInt(body, "gameID");
    }
}
