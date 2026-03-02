package service;

import chess.ChessBoard;
import exception.UserNotFoundException;
import model.*;
import request.*;
import result.ClearResult;
import dataaccess.*;
import result.*;

import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.UUID;

// This class will take RegisterRequest and call correct sequence of service class methods and return a Register Result
public class Service {

    public ClearResult clear () { // Will need to figure out how to check that db was cleared, then create ClearResponse instance and send it back
        AuthDAO.clearAllAuth();
        GameDAO.clearAllGame();
        UserDAO.clearAllUser();
        return new ClearResult();
    }

    public RegisterResult register(RegisterRequest request) {
        UserDAO.createUser(request.username, request.password, request.email);
        String authToken = generateToken();
        AuthDAO.createAuth(authToken, request.username);
        return new RegisterResult(authToken, request.username);
    }

    public LoginResult login(LoginRequest request) {
//        UserData user = UserDAO.getUser(request.username); //TODO should just need to pass in username
//        if (user == null) {
//            throw new UserNotFoundException("Error: Username not a registered user");
//        }
        String authToken = generateToken();
        AuthDAO.createAuth(authToken, request.username); // TODO, need username and way to generate auth token
        return new LoginResult(request.username, authToken); // TODO, need username and auth token to make LoginResult object
    }

    public LogoutResult logout(LogoutRequest request) {
        AuthData authData = AuthDAO.getAuth(request.authToken); // Just need to pass in authToken as arg
        AuthDAO.deleteAuth(authData.authToken); // Pass in auth token only
        return new LogoutResult();
    }

    public ListResult list(ListRequest request) {
        AuthData authData = AuthDAO.getAuth(request.authToken); // Just need auth token
        ArrayList<GameData> gameList = GameDAO.getGames();
        return new ListResult(gameList);
    }

    public CreateResult create(CreateRequest request) {
        AuthData authData = AuthDAO.getAuth(request.authToken); // Just need auth token as arg (it is to validate logged in user)
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        GameData gameData = new GameData(1234, "player1white", "player2black", request.gameName, board); // Needs gameid, whiteUsername, blackUsername, gameName, and ChessBoard object
        return new CreateResult(gameData.gameID);
    }

    public JoinResult join(JoinRequest request) {
        AuthData authData = AuthDAO.getAuth(request.authToken); // Just need auth token as arg (it is to validate logged in user)
        GameData gameData = GameDAO.getGame(request.gameID); // arg should be gameID
        GameDAO.joinGame(request.playerColor, request.gameID); // uses playerColor and gameID
        return new JoinResult();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
