package dataaccess;

import model.UserData;
import model.*;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();


    @Override
    public void clear(){
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData user){
        users.put(user.username(),user);
    }

    @Override
    public UserData getUser(String username){
        return users.get(username);
    }

    @Override
    public void deleteUser(String username){
        users.remove(username);
    }

    @Override
    public void createAuth(AuthData auth){
        auths.put(auth.authToken(),auth);
    }

    @Override
    public AuthData getAuth(String authToken){
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken){
        auths.remove(authToken);
    }

}
