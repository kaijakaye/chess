package service;

import datamodel.*;

public class UserService {

    public AuthData register(UserData user){
        return new AuthData(user.username(),generateAuthToken());
    }

    //go into the specs later to fix this
    private String generateAuthToken(){
        return "xyz";
    }
}
