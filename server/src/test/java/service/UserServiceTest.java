package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    //positive register test
    void registerSuccessful() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    //negative register test
    void registerInvalidUsername() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var user2 = new UserData("joe", "joe@j.com", "blahblahblah");
        Exception exception = assertThrows(Exception.class, () -> {userService.register(user2);});
        assertEquals("Error: already taken", exception.getMessage());
    }

    @Test//positive login test
    void loginSuccessful() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var authData2 = userService.login(user);
        assertNotNull(authData2);
        assertEquals(user.username(), authData2.username());
        assertFalse(authData2.authToken().isEmpty());
    }

    @Test
        //negative login test
    void loginInvalidUsername() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        Exception exception = assertThrows(Exception.class, () -> {userService.login(user);});
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test//positive logout test
    void logoutSuccessful() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var authData2 = userService.login(user);
        userService.logout(authData2.authToken());
        //can't logout twice
        Exception exception = assertThrows(Exception.class, () -> {userService.logout(authData2.authToken());});
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
        //negative logout test
    void logoutInvalidToken() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        Exception exception = assertThrows(Exception.class, () -> {userService.logout("invalid-token-xxx");});
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test//positive clear test
    void clearSuccessful() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var authData2 = userService.login(user);
        var game = new GameData(2,"myGame");
        db.clear();
        assertNull(db.getUser("joe"));
        assertNull(db.getGame(2));
    }

    @Test//positive createGame test
    void createGameSuccessful() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var game = new GameData(2,"myGame");
        userService.create(authData.authToken(),game);
        assertEquals(db.getGame(game.getGameID()),game);
    }

    @Test//negative createGame test
    void createGameInvalidName() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var game = new GameData(2, null);
        Exception exception = assertThrows(Exception.class, () -> {userService.create(authData.authToken(),game);});
        assertEquals("Error: bad request", exception.getMessage());
    }

    @Test//positive joinGame test
    void joinGameSuccessful() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var game = new GameData(2,"myGame");
        userService.create(authData.authToken(),game);
        userService.join(authData.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, game.getGameID()));
        var joinedGame = db.getGame(game.getGameID());
        assertNotNull(joinedGame);
        assertEquals("joe", joinedGame.getWhiteUsername());
    }

    @Test//negative joinGame test
    void joinGameInvalidAuthAndID() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var game = new GameData(2,"myGame");
        userService.create(authData.authToken(),game);
        Exception exception = assertThrows(Exception.class, () -> {userService.join(null, new JoinGameRequest(ChessGame.TeamColor.WHITE, game.getGameID()));});
        assertEquals("Error: unauthorized", exception.getMessage());
        Exception exception2 = assertThrows(Exception.class, () -> {userService.join(authData.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, 0));});
        assertEquals("Error: bad request", exception2.getMessage());
    }

    @Test//positive listGames test
    void listGamesSuccessful() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var game = new GameData(2,"myGame");
        userService.create(authData.authToken(),game);
        var result = userService.list(authData.authToken());
        assertNotNull(result);
    }

    @Test//negative listGames test
    void listGamesInvalidAuth() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        var game = new GameData(2,"myGame");
        userService.create(authData.authToken(),game);
        Exception exception = assertThrows(Exception.class, () -> {userService.list(null);});
        assertEquals("Error: unauthorized", exception.getMessage());
    }

}