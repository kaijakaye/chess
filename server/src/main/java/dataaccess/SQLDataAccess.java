package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.ListGamesResult;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDataAccess implements DataAccess {

    public SQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        changeDatabase(statement, user.username(), user.password(), user.email());
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

    //this is only used for updating/changing your database. not for fetching things
    private void changeDatabase(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            //this is making sure that the ?s get interpreted as strings
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    ps.setObject(i+1,params[i]);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user(
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL),
              PRIMARY KEY (`username`)
            """,
            """
            CREATE TABLE IF NOT EXISTS  game(
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              'game' TEXT,
              PRIMARY KEY (`gameID`)
              )
            """,
            """
            CREATE TABLE IF NOT EXISTS  auth(
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
              )
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
