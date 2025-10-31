package dataaccess;

import model.*;

import java.util.Collection;
import java.util.HashMap;

public interface DataAccess {

    void clear();
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
    void createGame(GameData game);
    GameData getGame(int gameID);
    ListGamesResult listGames();
    void updateGame(GameData game);
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);


}
