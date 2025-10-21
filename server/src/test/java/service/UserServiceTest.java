package service;

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

}