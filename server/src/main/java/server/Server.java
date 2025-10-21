package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

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
            //fix later?
            var msg = String.format("{ \"message\": \"Error: already taken\" }", ex.getMessage());
            ctx.status(403).result(msg);
        }

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
