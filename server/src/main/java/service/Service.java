package service;

import exception.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import dataaccess.*;
import result.*;
import request.*;

import java.util.ArrayList;
import java.util.UUID;


public class Service {
    private final MySqlAuthDAO authDAO = new MySqlAuthDAO();
    private final MySqlUserDAO userDAO = new MySqlUserDAO();
    private final MySqlGameDAO gameDAO = new MySqlGameDAO();

    public ClearResult clear () {
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
        UserData duplicateEmail = userDAO.getUserByEmail(request.email);
        if (duplicateEmail != null) {
            throw new InvalidRequestException(400, "Error: email already associated with an account");
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
        UserData user = userDAO.getUser(request.username);
        if (user == null) {
            throw new InvalidRequestException(401, "Error: Username not a registered user");
        }
//        if (!user.password.equals(request.password)) {
        if (!BCrypt.checkpw(request.password, user.password)) {
            throw new InvalidRequestException(401, "Error: Incorrect password");
        }
        String authToken = generateToken();
        authDAO.createAuth(authToken, request.username);
        return new LoginResult(request.username, authToken);
    }

    public LogoutResult logout(LogoutRequest request) throws InvalidRequestException {
        AuthData authData = authDAO.getAuth(request.authToken);
        if (authData == null) {
            throw new InvalidRequestException(401, "Error: Session not found");
        }
        authDAO.deleteAuth(authData.authToken);
        return new LogoutResult();
    }

    public ListResult list(ListRequest request) throws InvalidRequestException {
        AuthData authData = authDAO.getAuth(request.authToken);
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
        AuthData authData = authDAO.getAuth(request.authToken);
        if (authData == null) {
            throw new InvalidRequestException(401, "Error: Session not found");
        }
        GameData duplicateGameName = gameDAO.getGameByName(request.gameName);
        if (duplicateGameName != null) {
            throw new InvalidRequestException(401, "Error: Game name already taken");
        }
        gameDAO.createGame(request.gameName);
        GameData newGame = gameDAO.getGameByName(request.gameName);
        return new CreateResult(newGame.gameID);
    }

    public JoinResult join(JoinRequest request) throws InvalidRequestException {
        if (request.gameID == null) {
            throw new InvalidRequestException(400, "Error: GameID cannot be empty");
        }

        AuthData authData = authDAO.getAuth(request.authToken);
        if (authData == null) {
            throw new InvalidRequestException(401, "Error: Session not found");
        }

        GameData gameData = gameDAO.getGameByID(request.gameID);
        if (gameData == null) {
            throw new InvalidRequestException(400, "Error: Game not found");
        }

        // Only run team checks and database updates if they requested a color (i.e., not an observer)
        if (request.playerColor != null && !request.playerColor.isEmpty()) {
            if (!request.playerColor.equals("WHITE") && !request.playerColor.equals("BLACK")) {
                throw new InvalidRequestException(400, "Error: playerColor must be WHITE/BLACK");
            }

            if (request.playerColor.equals("WHITE")) {
                if (gameData.whiteUsername != null && !gameData.whiteUsername.equals(authData.username)) {
                    if (authData.username.equals(gameData.blackUsername)) {
                        throw new InvalidRequestException(403, "Error: User is already in this game as the black team");
                    }
                    throw new InvalidRequestException(403, "Error: White team color already taken");
                }
            }

            if (request.playerColor.equals("BLACK")) {
                if (gameData.blackUsername != null && !gameData.blackUsername.equals(authData.username)) {
                    if (authData.username.equals(gameData.whiteUsername)) {
                        throw new InvalidRequestException(403, "Error: User is already in this game as the white team");
                    }
                    throw new InvalidRequestException(403, "Error: Black team color already taken");
                }
            }

            // Finally, update the database for the player
            gameDAO.userJoin(request.playerColor, request.gameID, authData.username);
        }

        return new JoinResult();
    }

//    public JoinResult join(JoinRequest request) throws InvalidRequestException {
//        if (request.gameID == null) {
//            throw new InvalidRequestException(400, "Error: GameID cannot be empty");
//        }
////        if (request.playerColor == null) {
////            throw new InvalidRequestException(400, "Error: playerColor cannot be empty");
////        }
////        if (!request.playerColor.equals("WHITE")) {
////            if (!request.playerColor.equals("BLACK")) {
////                throw new InvalidRequestException(400, "Error: playerColor must be WHITE/BLACK");
////            }
////        }
//        AuthData authData = authDAO.getAuth(request.authToken);
//        if (authData == null) {
//            throw new InvalidRequestException(401, "Error: Session not found");
//        }
//        GameData gameData = gameDAO.getGameByID(request.gameID);
//        if (gameData == null) {
//            throw new InvalidRequestException(400, "Error: Game not found");
//        }
//        if (request.playerColor.equals("WHITE")) {
//            if (gameData.whiteUsername != null && !gameData.whiteUsername.equals(authData.username)) {
//                if (authData.username.equals(gameData.blackUsername)) {
//                    throw new InvalidRequestException(403, "Error: User is already in this game as the black team");
//                }
//                throw new InvalidRequestException(403, "Error: White team color already taken");
//            }
//        }
//        //
//        if (request.playerColor != null && !request.playerColor.isEmpty()) {
//            if (!request.playerColor.equals("WHITE") && !request.playerColor.equals("BLACK")) {
//                throw new InvalidRequestException(400, "Error: playerColor must be WHITE/BLACK");
//            }
//            if (request.playerColor.equals("WHITE")) {
//                if (gameData.whiteUsername != null && !gameData.whiteUsername.equals(authData.username)) {
//                    if (authData.username.equals(gameData.blackUsername)) {
//                        throw new InvalidRequestException(403, "Error: User is already in this game as black team");
//                    }
//                    throw new InvalidRequestException(403, "Error: White team color already taken");
//                }
//            }
//            if (request.playerColor.equals("BLACK")) {
//                if (gameData.blackUsername != null && !gameData.blackUsername.equals(authData.username)) {
//                    if (authData.username.equals(gameData.whiteUsername)) {
//                        throw new InvalidRequestException(403, "Error: User is already in this game as white team");
//                    }
//                    throw new InvalidRequestException(403, "Error: Black team color already taken");
//                }
//            }
//            gameDAO.userJoin(request.playerColor, request.gameID, authData.username);
//            return new JoinResult();
//        }
//        //
////        if (request.playerColor.equals("BLACK")) {
////            if (gameData.blackUsername != null && !gameData.blackUsername.equals(authData.username)) {
////                if (authData.username.equals(gameData.whiteUsername)) {
////                    throw new InvalidRequestException(403, "Error: User is already in this game as the white team");
////                }
////                throw new InvalidRequestException(403, "Error: Black team color already taken");
////            }
////        }
////        gameDAO.userJoin(request.playerColor, request.gameID, authData.username);
////        return new JoinResult();
//    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
