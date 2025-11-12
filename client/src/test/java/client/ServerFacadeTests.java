package client;

import model.*;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertTrue;


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

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    void register() throws Exception {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void login() throws Exception {
        UserData userEx = new UserData("player2", "password", "p1@email.com");
        var authData = facade.register(userEx);
        var authData2 = facade.login(userEx);
        assertTrue(authData.authToken().length() > 10);
    }

}
