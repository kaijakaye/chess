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
        server.delete("db",ctx -> ctx.result("{}"));
        //register
        server.post("user",ctx -> register(ctx));
        userService = new UserService(dataAccess);
        //login
        server.post("session",ctx -> login(ctx));
        //userService = new UserService(dataAccess);    do I need this?



    }

    private void register(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = userService.register(user);

            //call to the service and register
            //current authToken creation is temporary
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
