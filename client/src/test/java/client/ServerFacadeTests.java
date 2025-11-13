package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @BeforeEach
    public void clearTheServer() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    void registerSuccess() throws Exception {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginSuccess() throws Exception {
        UserData userEx = new UserData("player2", "password", "p1@email.com");
        var authData = facade.register(userEx);
        var authData2 = facade.login(userEx);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void logoutSuccess() throws Exception {
        var user = new UserData("logoutUser", "password", "logout@email.com");
        var auth = facade.register(user);

        // Logging out with a valid token should NOT throw an exception
        assertDoesNotThrow(() -> facade.logout(auth));
    }

    @Test
    void createGameSuccess() throws Exception {
        var user = new UserData("logoutUser", "password", "logout@email.com");
        var auth = facade.register(user);
        var game = new GameData(123, null, null, "myGame", new ChessGame());

        var gameStuff = facade.createGame(auth,game);
        assertNotNull(gameStuff);
    }

    @Test
    void createGameFailure() throws Exception {
        var user = new UserData("logoutUser", "password", "logout@email.com");
        var auth = facade.register(user);
        var game = new GameData(123, null, null, null, new ChessGame());

        Exception exception = assertThrows(Exception.class, () -> {facade.createGame(auth,game);});
        assertEquals("failure: { \"message\": \"Error: bad request\" }", exception.getMessage());
    }

    @Test
    void joinGameSuccess() throws Exception {
        var user = new UserData("logoutUser", "password", "logout@email.com");
        var auth = facade.register(user);
        var game = new GameData("myGame");

        var gameStuff = facade.createGame(auth,game);
        var req = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameStuff.getGameID());
        assertDoesNotThrow(() -> facade.joinGame(auth,req));
    }

    @Test
    void listGamesSuccess() throws Exception {
        var user = new UserData("logoutUser", "password", "logout@email.com");
        var auth = facade.register(user);
        var game = new GameData("myGame");

        var gameStuff = facade.createGame(auth,game);
        var gameList = facade.listGames(auth);
        assertNotNull(gameList);
        assertFalse(gameList.games().isEmpty());
    }

    @Test
    void listGamesFailure() throws Exception {
        var user = new UserData("logoutUser", "password", "logout@email.com");
        var auth = facade.register(user);
        var gameList = facade.listGames(auth);
        assertNotNull(gameList);
        assertTrue(gameList.games().isEmpty());
    }

}
