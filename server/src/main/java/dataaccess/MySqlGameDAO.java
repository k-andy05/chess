package dataaccess;

import chess.ChessBoard;
import exception.DataAccessException;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import com.google.gson.Gson;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO () { configureTable(); }

    private void configureTable() throws DataAccessException {
        // TODO may need to do something to game column type or serializing it or something
        String statement = """
                CREATE TABLE IF NOT EXISTS gamedata (
                    gameID INT AUTO_INCREMENT PRIMARY KEY,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255),
                    game TEXT
                );
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAllGame() throws DataAccessException {
        String statement = "TRUNCATE TABLE gamedata";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
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
                    GameData game = new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            new ChessBoard()
                    );
                    games.add(game);
                }
                return games;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void userJoin(String playerColor, int GameID, String username) throws DataAccessException {
        String statement = null;
        if ("WHITE".equals(playerColor)) {
            statement = "UPDATE gamedata SET whiteUsername = ? WHERE gameID = ?";
        } else {
            statement = "UPDATE gamedata SET blackUsername = ? WHERE gameID = ?";
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            ps.setInt(2, GameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //TODO fix GameData return to return GameData object
    @Override
    public GameData getGameByName(String gameName) throws DataAccessException {
        String statement = "SELECT * FROM gamedata WHERE gameName = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, gameName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), new ChessBoard());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    //TODO same error as above return statement
    @Override
    public GameData getGameByID(Integer gameID) throws DataAccessException {
        String statement = "SELECT * FROM gamedata WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), new ChessBoard());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void createGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO gamedata (gameName) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, gameName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
