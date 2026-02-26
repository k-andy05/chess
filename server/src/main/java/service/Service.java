package service;

import io.javalin.http.CreatedResponse;
import model.*;
import request.*;
import result.ClearResult;
import dataaccess.*;
import result.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;

// This class will take RegisterRequest and call correct sequence of service class methods and return a Register Result
public class Service {

    public ClearResult clear () { // Will need to figure out how to check that db was cleared, then create ClearResponse instance and send it back
        DataAccess.clearAll();
        return new ClearResult();
    }

    public RegisterResult register(RegisterRequest request) {
        DataAccess.createUser(request); // Idk if i have to pass these in as three arguments or just as a request?
        DataAccess.createAuth(request); // TODO just need the username out of the request object along with a type of generated auth token
        RegisterResult result = new RegisterResult(request); // TODO will need to provide auth token as well as username
        return result;
    }

    public LoginResult login(LoginRequest request) {
        UserData user = new UserData(DataAccess.getUser(request)); //TODO should just need to pass in username
        // TODO Validate password
        DataAccess.createAuth(request); // TODO, need username and way to generate auth token
        LoginResult result = new LoginResult(request); // TODO, need username and auth token to make LoginResult object
        return result;
    }

    public LogoutResult logout(LogoutRequest request) {
        AuthData authData = new AuthData(DataAccess.getAuth(request)); // Just need to pass in authToken as arg
        DataAccess.deleteAuth(authData); // Pass in auth token only
        LogoutResult result = new LogoutResult();
        return result;
    }

    public ListResult list(ListRequest request) {
        AuthData authData = new AuthData(DataAccess.getAuth(request)); // Just need auth token
        ArrayList<GameData> gameList = DataAccess.getGames();
        ListResult result = new ListResult(gameList);
        return result;
    }

    public CreateResult create(CreateRequest request) {
        AuthData authData = new AuthData(DataAccess.getAuth(request)); // Just need auth token as arg (it is to validate logged in user)
        GameData gameData = new GameData(request); // Needs gameid, whiteUsername, blackUsername, gameName, and ChessBoard object
        CreateResult result = new CreateResult(gameData);
        return result;
    }

    public JoinResult join(JoinRequest request) {
        AuthData authData = new AuthData(DataAccess.getAuth(request)); // Just need auth token as arg (it is to validate logged in user)
        GameData gameData = DataAccess.getGame(request); // arg should be gameID
        DataAccess.userAccess(request); // uses playerColor and gameID
        JoinResult result = new JoinResult();
        return result;
    }
}
