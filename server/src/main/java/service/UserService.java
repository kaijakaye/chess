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
            throw new BadRequestException();
        }

        if(dataAccess.getUser(user.username())!=null){
            throw new AlreadyTakenException();
        }

        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.createAuth(authData);
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
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws Exception{
        AuthData auth = dataAccess.getAuth(authToken);

        //if the authData is null
        if(auth==null){
            throw new UnauthorizedException();
        }

        //if the username hasn't been registered already
        if(auth.username()==null){
            throw new UnauthorizedException();
        }
        dataAccess.deleteAuth(authToken);
    }


    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
