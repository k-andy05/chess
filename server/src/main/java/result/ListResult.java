package result;

import model.*;
import java.util.ArrayList;

public class ListResult {
    private ArrayList<GameData> gameList;

    public ListResult(ArrayList<GameData> gameList) {
        this.gameList = gameList;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"games\":[");
        for (int i = 0; i < gameList.size(); i++) {
            GameData game = gameList.get(i);
            sb.append("{")
                    .append("\"gameID\":").append(game.gameID).append(",")
                    .append("\"whiteUsername\":")
                    .append(game.whiteUsername == null ? "null"
                            : "\"" + game.whiteUsername + "\"")
                    .append(",")
                    .append("\"blackUsername\":")
                    .append(game.blackUsername == null ? "null"
                            : "\"" + game.blackUsername + "\"")
                    .append(",")
                    .append("\"gameName\":")
                    .append(game.gameName == null ? "null"
                            : "\"" + game.gameName + "\"")
                    .append("}");
            if (i < gameList.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
