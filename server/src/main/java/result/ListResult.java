package result;

import model.*;
import java.util.ArrayList;

public class ListResult {
    private ArrayList<GameData> gameList;

    public ListResult(ArrayList<GameData> gameList) {
        this.gameList = gameList;
    }

    public String toJson() {return "{}";}
}
