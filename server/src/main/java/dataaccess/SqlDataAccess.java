package dataaccess;

import model.AuthData;
import model.GameData;
import model.ListGamesResult;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlDataAccess implements DataAccess {

    public SqlDataAccess () throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }


    @Override
    public void createGame(GameData game) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public ListGamesResult listGames() {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user(
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,),
            CREATE TABLE IF NOT EXISTS  game(
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              'game' TEXT,
              )
            CREATE TABLE IF NOT EXISTS  auth(
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              ),
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
