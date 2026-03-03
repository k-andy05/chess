package result;

public class CreateResult {
    public int gameID;

    public CreateResult(int gameID) {
        this.gameID = gameID;
    }

    public String toJson() {
        return "{\"gameID\":" + gameID + "}";
    }
}
