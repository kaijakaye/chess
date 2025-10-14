package service;

import dataaccess.DataAccess;
import datamodel.*;

import javax.xml.crypto.Data;

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

    //go into the specs later to fix this
    private String generateAuthToken(){
        return "xyz";
    }
}
