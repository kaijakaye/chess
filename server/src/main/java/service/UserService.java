package service;

import dataaccess.DataAccess;

import java.util.UUID;

import model.*;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws Exception{
        if(dataAccess.getUser(user.username())!=null){
            //fix this later via petshop example
            throw new Exception("already exists");
        }

        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateAuthToken());
        return authData;
    }


    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
