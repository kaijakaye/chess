package dataaccess;

import model.AuthData;
import model.GameData;
import model.ListGamesResult;
import model.UserData;

public class SqlDataAccess implements DataAccess {

    public SqlDataAccess () throws DataAccessException {
        DatabaseManager.createDatabase();
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
}
