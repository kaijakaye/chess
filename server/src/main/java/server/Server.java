package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        //clear
        server.delete("db",ctx -> clear(ctx));
        //register
        server.post("user",ctx -> register(ctx));
        //login
        server.post("session",ctx -> login(ctx));
        //logout
        server.delete("session",ctx -> logout(ctx));
        //create game
        server.post("game",ctx -> create(ctx));
        userService = new UserService(dataAccess);

    }

    private void clear(Context ctx){
        userService.clear();
        ctx.result();
    }

    private void register(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = userService.register(user);

            //call to the service and register
            ctx.result(serializer.toJson(authData));
        }
        catch(Exception ex){
            var msg = String.format("{ \"message\": \"%s\" }", ex.getMessage());
            ctx.status(getStatusCode(ex)).result(msg);

        }

    }

    private void login(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = userService.login(user);

            //call to the service and login
            ctx.result(serializer.toJson(authData));
        }
        catch(Exception ex){
            var msg = String.format("{ \"message\": \"%s\" }", ex.getMessage());
            ctx.status(getStatusCode(ex)).result(msg);

        }

    }

    private void logout(Context ctx){
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.result();
        }
        catch(Exception ex){
            var msg = String.format("{ \"message\": \"%s\" }", ex.getMessage());
            ctx.status(getStatusCode(ex)).result(msg);
        }

    }

    private void create(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var game = serializer.fromJson(reqJson, GameData.class);

            String authToken = ctx.header("authorization");
            var gameData = userService.create(authToken, game);

            //call to the service and login
            ctx.result(serializer.toJson(gameData));
        }
        catch(Exception ex){
            var msg = String.format("{ \"message\": \"%s\" }", ex.getMessage());
            ctx.status(getStatusCode(ex)).result(msg);

        }
    }

    private int getStatusCode(Exception ex){
        return switch(ex){
            case BadRequestException ignore -> 400;
            case UnauthorizedException ignore -> 401;
            case AlreadyTakenException ignore -> 403;
            default -> 500;
        };
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
