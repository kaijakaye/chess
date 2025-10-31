package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    @Test//positive register test
    void registerSuccessful() throws Exception {
        DataAccess db = new SQLDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }
}
