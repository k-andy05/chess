package service;

import io.javalin.http.CreatedResponse;
import model.*;
import request.*;
import result.ClearResult;
import dataaccess.*;
import result.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.UUID;

// This class will take RegisterRequest and call correct sequence of service class methods and return a Register Result
public class Service {

    public ClearResult clear () { // Will need to figure out how to check that db was cleared, then create ClearResponse instance and send it back
        AuthAccess.clearAllAuth();
        GameAccess.clearAllGame();
        UserAccess.clearAllUser();
        return new ClearResult();
    }

    public RegisterResult register(RegisterRequest request) {
        UserAccess.createUser(request.username, request.password, request.email);
        AuthAccess.createAuth(request.username);
        String authToken = AuthToken();
        return new RegisterResult(authToken, request.username);
    }

    public LoginResult login(LoginRequest request) {
        UserData user = new UserData(UserAccess.getUser(request.username)); //TODO should just need to pass in username
        // TODO Validate password
        String authToken = AuthToken();
        AuthAccess.createAuth(authToken, request.username); // TODO, need username and way to generate auth token
        return new LoginResult(request.username, authToken); // TODO, need username and auth token to make LoginResult object
    }

    public LogoutResult logout(LogoutRequest request) {
        AuthData authData = new AuthData(AuthAccess.getAuth(request.authToken)); // Just need to pass in authToken as arg
        AuthAccess.deleteAuth(authData.authToken); // Pass in auth token only
        return new LogoutResult();
    }

    public ListResult list(ListRequest request) {
        AuthData authData = new AuthData(AuthAccess.getAuth(request.authToken)); // Just need auth token
        ArrayList<GameData> gameList = GameAccess.getGames();
        return new ListResult(gameList);
    }

    public CreateResult create(CreateRequest request) {
        AuthData authData = new AuthData(AuthAccess.getAuth(request)); // Just need auth token as arg (it is to validate logged in user)
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

    private String AuthToken() {
        return UUID.randomUUID().toString();
    }
}
