package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private SQLDataAccess dataAccess;
    private UserData testUser;

    @BeforeEach
    @DisplayName("clear - positive case")
    void setup() throws DataAccessException {
        dataAccess = new SQLDataAccess();
        dataAccess.clear(); // optional if you have this method
        testUser = new UserData("testUser", "pw123", "ex@example.com");
    }

    @Test
    @DisplayName("createUser - positive case")
    void createUserSuccessful() throws Exception {
        dataAccess.createUser(testUser);

        UserData found = dataAccess.getUser(testUser.username());
        assertNotNull(found);
        assertEquals(testUser.username(), found.username());
        assertEquals(testUser.email(), found.email());
    }

    @Test
    @DisplayName("createUser - negative case (duplicate username)")
    void createUserError() throws Exception {
        dataAccess.createUser(testUser);

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(testUser);
        });
    }

    @Test
    @DisplayName("getUser - positive case")
    void getUserSuccessful() throws Exception {
        dataAccess.createUser(testUser);
        UserData retrieved = dataAccess.getUser(testUser.username());

        assertNotNull(retrieved);
        assertEquals(testUser.username(), retrieved.username());
        assertNotNull(retrieved.password());
        //make sure it got hashed
        assertNotEquals(testUser.password(), retrieved.password());
        assertEquals(testUser.email(), retrieved.email());

    }

    @Test
    @DisplayName("getUser - negative case (retrieving nonexisting user)")
    void getUserError() throws Exception {
        var retrieved = dataAccess.getUser("ghostUser");

        assertNull(retrieved);
    }

    void createGameSuccessful() throws Exception {

    }

    void createGameError() throws Exception {

    }

    void getGameSuccessful() throws Exception {

    }

    void getGameError() throws Exception {

    }


/*
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    ListGamesResult listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void createAuth(AuthData auth) throws DataAccessException ;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

 */
}
