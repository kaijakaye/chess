package service;

import dataaccess.DataAccess;

import java.util.Objects;
import java.util.UUID;

import model.*;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws Exception{
        if(user.username()==null || user.password()==null || user.email()==null){
            throw new Exception("Error: bad request");
        }

        if(dataAccess.getUser(user.username())!=null){
            throw new Exception("Error: already taken");
        }

        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateAuthToken());
        return authData;
    }

    public AuthData login(UserData user) throws Exception{
        //if they didn't put in a username or password
        if(user.username()==null || user.password()==null){
            throw new BadRequestException();
        }

        //if the username hasn't been registered already
        if(dataAccess.getUser(user.username())==null){
            throw new UnauthorizedException();
        }

        //if the password they entered doesn't match the username
        if(!user.password().equals(dataAccess.getUser(user.username()).password())){
            throw new UnauthorizedException();
        }

        UserData uData = dataAccess.getUser(user.username());
        var authData = new AuthData(user.username(), generateAuthToken());
        return authData;
    }


    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
