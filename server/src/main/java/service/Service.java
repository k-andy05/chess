package service;

import chess.ChessBoard;
import chess.InvalidMoveException;
import exception.*;
import model.*;
import request.*;
import result.ClearResult;
import dataaccess.*;
import result.*;

import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

// This class will take RegisterRequest and call correct sequence of service class methods and return a Register Result
public class Service {
    private final AuthDAO authDAO = new AuthDAO();
    private final UserDAO userDAO = new UserDAO();
    private final GameDAO gameDAO = new GameDAO();

    public ClearResult clear () { // Will need to figure out how to check that db was cleared, then create ClearResponse instance and send it back
        authDAO.clearAllAuth();
        gameDAO.clearAllGame();
        userDAO.clearAllUser();
        return new ClearResult();
    }

    public RegisterResult register(RegisterRequest request) throws InvalidRequestException {
        UserData checkUser= userDAO.getUser(request.username);
        if (checkUser != null) {
            throw new InvalidRequestException(403, "Error: username already taken");
        }
        if (request.username == null || request.password == null) {
            throw new InvalidRequestException(400, "Error: username or password not entered");
        }
        userDAO.createUser(request.username, request.password, request.email);
        String authToken = generateToken();
        authDAO.createAuth(authToken, request.username);
        return new RegisterResult(authToken, request.username);
    }

    public LoginResult login(LoginRequest request) throws InvalidRequestException {
        if (request.username == null || request.password == null) {
            throw new InvalidRequestException(400, "Error: Username not provided correctly");
        }
        UserData user = userDAO.getUser(request.username); //TODO should just need to pass in username
        if (user == null) {
            throw new InvalidRequestException(401, "Error: Username not a registered user");
        }
        if (!user.password.equals(request.password)) {
            throw new InvalidRequestException(401, "Error: Incorrect password");
        }
        String authToken = generateToken();
        authDAO.createAuth(authToken, request.username); // TODO, need username and way to generate auth token
        return new LoginResult(request.username, authToken); // TODO, need username and auth token to make LoginResult object
    }

    public LogoutResult logout(LogoutRequest request) throws InvalidRequestException {
        AuthData authData = authDAO.getAuth(request.authToken); // Just need to pass in authToken as arg
        if (authData == null) {
            throw new InvalidRequestException(401, "Error: Session not found");
        }
        authDAO.deleteAuth(authData.authToken); // Pass in auth token only
        return new LogoutResult();
    }

    public ListResult list(ListRequest request) throws InvalidRequestException {
        AuthData authData = authDAO.getAuth(request.authToken); // Just need auth token
        if (authData == null) {
            throw new InvalidRequestException(401, "Error: Session not found");
        }
        ArrayList<GameData> games = gameDAO.getGames();
        return new ListResult(games);
    }

    public CreateResult create(CreateRequest request) throws InvalidRequestException {
        if (request.authToken == null || request.gameName == null) {
            throw new InvalidRequestException(400, "Error: authToken and gameName cannot be empty");
        }
        AuthData authData = authDAO.getAuth(request.authToken); // Just need auth token as arg (it is to validate logged in user)
        if (authData == null) {
            throw new InvalidRequestException(401, "Error: Session not found");
        }
        gameDAO.createGame(request.gameName);
        GameData newGame = gameDAO.getGameByName(request.gameName);
        return new CreateResult(newGame.gameID);
    }

    public JoinResult join(JoinRequest request) throws InvalidRequestException {
        Set<String> validColors = Set.of("WHITE", "BLACK");
        if (!validColors.contains(request.playerColor)) {
            throw new InvalidRequestException(400, "Error: Invalid team color");
        }
        if (request.authToken == null || request.playerColor == null) {
            throw new InvalidRequestException(400, "Error: AuthToken and PlayerColor and GameID cannot be empty");
        }
        AuthData authData = authDAO.getAuth(request.authToken); // Just need auth token as arg (it is to validate logged in user)
        if (authData == null) {
            throw new InvalidRequestException(401, "Error: Session not found");
        }
        GameData gameData = gameDAO.getGameByID(request.gameID); // arg should be gameID
        if (gameData == null) {
            throw new InvalidRequestException(400, "Error: Game not found");
        }
        if (request.playerColor.equals("WHITE")) {
            if (gameData.whiteUsername != null) {
                throw new InvalidRequestException(403, "Error: White team color already taken");
            }
        }
        if (request.playerColor.equals("BLACK")) {
            if (gameData.blackUsername != null) {
                throw new InvalidRequestException(403, "Error: Black team color already taken");
            }
        }
        gameDAO.userJoin(request.playerColor, request.gameID, authData.username); // uses playerColor and gameID
        return new JoinResult();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
