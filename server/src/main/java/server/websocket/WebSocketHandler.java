package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;

import io.javalin.websocket.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            String rawJson = ctx.message();
            UserGameCommand command = new Gson().fromJson(rawJson, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> { connect(command, ctx.session); }
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(rawJson, MakeMoveCommand.class);
                    makeMove(moveCommand, ctx.session);
                }
                case LEAVE -> { leave(command, ctx.session); }
                case RESIGN -> { resign(command, ctx.session); }
            }
        } catch (IOException | ResponseException ex) {
            ex.printStackTrace();
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, Session session) throws IOException, ResponseException {
        MySqlAuthDAO authDAO = new MySqlAuthDAO();
        AuthData authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            String messageStr = "Error: auth token not valid";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        MySqlGameDAO gameDAO = new MySqlGameDAO();
        GameData gameData = gameDAO.getGameByID(command.getGameID());
        if (gameData == null) {
            String messageStr = "Error: game does not exist";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        String teamColor;
        String messageStr;
        if (authData.username.equals(gameData.whiteUsername)) {
            teamColor = "white";
            messageStr = String.format("%s has joined the game on the %s team", authData.username, teamColor);
        } else if (authData.username.equals(gameData.blackUsername)) {
            teamColor = "black";
            messageStr = String.format("%s has joined the game on the %s team", authData.username, teamColor);
        } else {
            messageStr = String.format("%s has joined the game as an observer", authData.username);
        }
        NotificationMessage notificationMessage = new NotificationMessage(messageStr);
        try {
            connections.add(session, command.getGameID());
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game);
            sendMessage(session, loadGameMessage);
            System.out.print("Just sent LoadGameMessage to specific user");
            connections.broadcast(session, notificationMessage, command.getGameID());
            System.out.print("Just broadcasted connect message to everyone except user who connected");
        } catch (IOException | ResponseException e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Error: " + e.getMessage());
        }
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException, InvalidMoveException, ResponseException { // TODO figure out later after message from client is built and handled...
        MySqlAuthDAO authDAO = new MySqlAuthDAO();
        AuthData authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            String messageStr = "Error: auth token not valid";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        MySqlGameDAO gameDAO = new MySqlGameDAO();
        GameData gameData = gameDAO.getGameByID(command.getGameID());
        if (gameData == null) {
            String messageStr = "Error: game does not exist";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        if (gameData.game.isGameOver()) {
            String messageStr = "Error: Game has already ended. No more moves are allowed";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        if (!authData.username.equals(gameData.whiteUsername) && !authData.username.equals(gameData.blackUsername)) {
            String messageStr = "Error: observer not permitted to make a move";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        if (gameData.game.getTeamTurn() == ChessGame.TeamColor.WHITE && authData.username.equals(gameData.blackUsername)
        || gameData.game.getTeamTurn() == ChessGame.TeamColor.BLACK && authData.username.equals(gameData.whiteUsername)) {
            String messageStr = "Error: It is not your turn to make moves yet";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        MakeMoveCommand moveCommand = (MakeMoveCommand) command;
        ChessMove chessMove = moveCommand.getChessMove();
        try {
            gameData.game.makeMove(chessMove);
        } catch (InvalidMoveException e) {
            String messageStr = "Error: Move not permitted";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        gameDAO.updateGame(gameData);
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game);
        sendMessage(session, loadGameMessage);
        connections.broadcast(session, loadGameMessage, command.getGameID());
        String messageStr = String.format("%s: a6 to b4", authData.username);
        NotificationMessage notificationMessage = new NotificationMessage(messageStr);
        sendMessage(session, notificationMessage);
        connections.broadcast(session, notificationMessage, command.getGameID());
    }

    private void leave(UserGameCommand command, Session session) throws IOException, ResponseException {
        MySqlAuthDAO authDAO = new MySqlAuthDAO();
        AuthData authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            String messageStr = "Error: auth token not valid";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        MySqlGameDAO gameDAO = new MySqlGameDAO();
        GameData gameData = gameDAO.getGameByID(command.getGameID());
        if (gameData == null) {
            String messageStr = "Error: game does not exist";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        if (authData.username.equals(gameData.whiteUsername)) {
            GameData gameDataNew = new GameData(gameData.gameID, null, gameData.blackUsername, gameData.gameName, gameData.game);
            gameDAO.updateGame(gameDataNew);
        } else if (authData.username.equals(gameData.blackUsername)) {
            GameData gameDataNew = new GameData(gameData.gameID, gameData.whiteUsername, null, gameData.gameName, gameData.game);
            gameDAO.updateGame(gameDataNew);
        }
        String messageStr = String.format("%s has left the game", authData.username);
        NotificationMessage notificationMessage = new NotificationMessage(messageStr);
        try {
            connections.remove(session, command.getGameID());
            connections.broadcast(session, notificationMessage, command.getGameID());
            System.out.print("Just broadcasted message to everyone except user who left that user left game");
        } catch (IOException | ResponseException e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Error: " + e.getMessage());
        }
    }

    private void resign(UserGameCommand command, Session session) throws IOException, ResponseException {
        MySqlAuthDAO authDAO = new MySqlAuthDAO();
        AuthData authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            String messageStr = "Error: auth token not valid";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        MySqlGameDAO gameDAO = new MySqlGameDAO();
        GameData gameData = gameDAO.getGameByID(command.getGameID());
        if (gameData == null) {
            String messageStr = "Error: game does not exist";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        if (gameData.game.isGameOver()) {
            String messageStr = "Error: Game has already ended";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
            return;
        }
        if (authData.username.equals(gameData.whiteUsername) || authData.username.equals(gameData.blackUsername)) {
            gameData.game.setGameOver(true);
            gameDAO.updateGame(gameData);
            String messageStr = String.format("%s has resigned", authData.username);
            NotificationMessage notificationMessage = new NotificationMessage(messageStr);
            connections.broadcast(session, notificationMessage, command.getGameID());
        } else {
            String messageStr = "Error: observer not permitted to resign a game";
            ErrorMessage errorMessage = new ErrorMessage(messageStr);
            sendMessage(session, errorMessage);
        }

    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }
}
