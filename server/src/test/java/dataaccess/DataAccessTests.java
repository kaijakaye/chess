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
    private AuthData testAuth;
    private GameData testGame;

    @BeforeEach
    @DisplayName("clear - positive case")
    void setup() throws DataAccessException {
        dataAccess = new SQLDataAccess();
        dataAccess.clear(); // optional if you have this method
        testUser = new UserData("testUser", "pw123", "ex@example.com");
        testAuth = new AuthData("token123","testUser");
        testGame = new GameData(123, null, null, "myGame", new ChessGame());
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

    @Test
    @DisplayName("createGame - positive case")
    void createGameSuccessful() throws Exception {
        dataAccess.createGame(testGame);

        GameData found = dataAccess.getGame(testGame.getGameID());
        assertNotNull(found);
        assertEquals(testGame.getGameID(), found.getGameID());
        assertEquals(testGame.getGameName(), found.getGameName());
    }

    @Test
    @DisplayName("createGame - negative case (duplicate game info)")
    void createGameError() throws Exception {
        dataAccess.createGame(testGame);

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createGame(testGame);
        });
    }

    @Test
    @DisplayName("getGame - positive case")
    void getGameSuccessful() throws Exception {
        dataAccess.createGame(testGame);
        GameData retrieved = dataAccess.getGame(testGame.getGameID());

        assertNotNull(retrieved);
        assertEquals(testGame.getGameID(), retrieved.getGameID());
        assertEquals(testGame.getGameName(), retrieved.getGameName());
        assertEquals(testGame.getGame(), retrieved.getGame());
    }

    @Test
    @DisplayName("getGame - negative case (retrieving nonexisting game)")
    void getGameError() throws Exception {
        var retrieved = dataAccess.getGame(41948);

        assertNull(retrieved);
    }

    @Test
    @DisplayName("createAuth - positive case")
    void createAuthSuccessful() throws Exception {
        dataAccess.createAuth(testAuth);

        AuthData found = dataAccess.getAuth(testAuth.authToken());
        assertNotNull(found);
        assertEquals(testAuth.authToken(), found.authToken());
        assertEquals(testAuth.username(), found.username());
    }

    @Test
    @DisplayName("createAuth - negative case (duplicate authToken)")
    void createAuthError() throws Exception {
        dataAccess.createAuth(testAuth);

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createAuth(testAuth);
        });
    }

    @Test
    @DisplayName("getAuth - positive case")
    void getAuthSuccessful() throws Exception {
        dataAccess.createAuth(testAuth);
        AuthData retrieved = dataAccess.getAuth(testAuth.authToken());

        assertNotNull(retrieved);
        assertEquals(testAuth.authToken(), retrieved.authToken());
        assertEquals(testAuth.username(), retrieved.username());

    }

    @Test
    @DisplayName("getAuth - negative case (retrieving nonexisting auth)")
    void getAuthError() throws Exception {
        var retrieved = dataAccess.getAuth("ghostAuth");

        assertNull(retrieved);
    }

    @Test
    @DisplayName("deleteAuth - positive case")
    void deleteAuthSuccessful() throws Exception {
        dataAccess.createAuth(testAuth);
        dataAccess.deleteAuth(testAuth.authToken());

        var retrieved = dataAccess.getAuth(testAuth.authToken());
        assertNull(retrieved);

    }

    @Test
    @DisplayName("deleteAuth - negative case")
    void deleteAuthError() throws Exception {
        String fakeToken = "nonexistentToken123";

        assertDoesNotThrow(() -> dataAccess.deleteAuth(fakeToken));
        var result = dataAccess.getAuth(fakeToken);
        assertNull(result);
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
