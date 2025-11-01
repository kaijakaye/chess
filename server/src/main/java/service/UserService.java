package service;
import chess.ChessGame;
import dataaccess.DataAccess;

import java.util.Collection;
import java.util.UUID;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final DataAccess dataAccess;
    private int gameIDCounter;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
        gameIDCounter = 1;
    }

    public void clear() throws Exception{
        dataAccess.clear();
    }

    public AuthData register(UserData user) throws Exception{
        if(user.username()==null || user.password()==null || user.email()==null){
            throw new BadRequestException();
        }

        if(dataAccess.getUser(user.username())!=null){
            throw new AlreadyTakenException();
        }

        dataAccess.createUser(user);
        var authData = new AuthData(generateAuthToken(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws Exception{
        //if they didn't put in a username or password
        if(user.username()==null || user.password()==null){
            throw new BadRequestException();
        }

        var existingUser = dataAccess.getUser(user.username());

        //if the username hasn't been registered already
        if(existingUser==null){
            throw new UnauthorizedException();
        }

        //if the password they entered doesn't match the username
        if(!BCrypt.checkpw(user.password(), existingUser.password())){
            throw new UnauthorizedException();
        }

        var authData = new AuthData(generateAuthToken(), user.username());
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

    public GameData create(String authToken, GameData game) throws Exception{
        if(game.getGameName()==null){
            throw new BadRequestException();
        }
        var authData = dataAccess.getAuth(authToken);
        if(authData==null){
            throw new UnauthorizedException();
        }

        game.setGameID(gameIDCounter);
        ++gameIDCounter;
        dataAccess.createGame(game);
        return game;
    }

    public void join(String authToken, JoinGameRequest gameRequest) throws Exception{
        var authData = dataAccess.getAuth(authToken);

        if(authData==null){
            throw new UnauthorizedException();
        }

        var gameData = dataAccess.getGame(gameRequest.gameID());

        if(gameData==null){
            throw new BadRequestException();
        }

        if(gameRequest.playerColor()== ChessGame.TeamColor.WHITE){
            if(gameData.getWhiteUsername()!=null){
                throw new AlreadyTakenException();
            }
            gameData.setWhiteUsername(authData.username());
        }
        else if(gameRequest.playerColor()== ChessGame.TeamColor.BLACK){
            if(gameData.getBlackUsername()!=null){
                throw new AlreadyTakenException();
            }
            gameData.setBlackUsername(authData.username());
        }
        //if the team color is null
        else{
            throw new BadRequestException();
        }

        dataAccess.updateGame(gameData);
    }

    public ListGamesResult list(String authToken) throws Exception{
        var authData = dataAccess.getAuth(authToken);
        if(authData==null){
            throw new UnauthorizedException();
        }
        ListGamesResult result = dataAccess.listGames();
        return result;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
