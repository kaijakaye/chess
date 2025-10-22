package dataaccess;

import model.*;

import java.util.HashMap;

public interface DataAccess {

    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    void createGame(GameData game);
    //GameData getGame(int gameID);
    //list games
    //void updateGame() not sure of arguments
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);


}
