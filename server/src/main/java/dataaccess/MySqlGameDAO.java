package dataaccess;

import chess.ChessGame;
import exception.DataAccessException;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import com.google.gson.Gson;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO () { configureTable(); }

    private void configureTable() throws DataAccessException {
        String statement = """
                CREATE TABLE IF NOT EXISTS gamedata (
                    gameID INT AUTO_INCREMENT PRIMARY KEY,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255) NOT NULL UNIQUE,
                    game TEXT
                );
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to initialize gamedata table");
        }
    }

    @Override
    public void clearAllGame() throws DataAccessException {
        String statement = "TRUNCATE TABLE gamedata";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to clear gamedata table");
        }
    }

    @Override
    public ArrayList<GameData> getGames() throws DataAccessException {
        String statement = "SELECT * FROM gamedata";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<GameData> games = new ArrayList<>();
                while (rs.next()) {
                    games.add(formatResultSet(rs));
                }
                return games;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get list of all gamedata entries");
        }
    }

    @Override
    public void userJoin(String playerColor, int gameID, String username) throws DataAccessException {
        String statement = null;
        if ("WHITE".equals(playerColor)) {
            statement = "UPDATE gamedata SET whiteUsername = ? WHERE gameID = ?";
        } else {
            statement = "UPDATE gamedata SET blackUsername = ? WHERE gameID = ?";
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            ps.setInt(2, gameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to join user to game in gamedata table");
        }
    }

    @Override
    public GameData getGameByName(String gameName) throws DataAccessException {
        String statement = "SELECT * FROM gamedata WHERE gameName = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, gameName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return formatResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get game data by gameName");
        }
        return null;
    }

    @Override
    public GameData getGameByID(Integer gameID) throws DataAccessException {
        String statement = "SELECT * FROM gamedata WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return formatResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get game data by gameID");
        }
        return null;
    }

    @Override
    public void createGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO gamedata (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ChessGame game = new ChessGame();
            game.getBoard().resetBoard();
            Gson gson = new Gson();
            String gameJson = gson.toJson(game);
            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, gameName);
            ps.setString(4, gameJson);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to create new game entry in gamedata");
        }
    }

//    @Override
    public void updateGame(GameData gameDataNew) throws DataAccessException {
        String statement = "UPDATE gamedata SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ChessGame newGame = gameDataNew.game;
            Gson gson = new Gson();
            String newGameJson = gson.toJson(newGame);
//            GameData gameDataOrig = this.getGameByID(gameDataNew.gameID);
            ps.setString(1, gameDataNew.whiteUsername);
            ps.setString(2, gameDataNew.blackUsername);
            ps.setString(3, gameDataNew.gameName);
            ps.setString(4, newGameJson);
            ps.setInt(5, gameDataNew.gameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to update game entry in gamedata");
        }
    }

    private GameData formatResultSet(ResultSet rs) throws SQLException {
        Gson gson = new Gson();
        ChessGame chessGame = gson.fromJson(rs.getString("game"), ChessGame.class);
        return new GameData(
                rs.getInt("gameID"),
                rs.getString("whiteUsername"),
                rs.getString("blackUsername"),
                rs.getString("gameName"),
//                chessGame.getBoard()
                chessGame
        );
    }
}
