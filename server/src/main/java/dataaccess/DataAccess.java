package dataaccess;

import model.*;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;

public interface DataAccess {

    void clear();
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void createGame(GameData game);
    GameData getGame(int gameID);
    ListGamesResult listGames();
    void updateGame(GameData game);
    void createAuth(AuthData auth) throws DataAccessException ;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;


}
