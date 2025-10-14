package server;

import com.google.gson.Gson;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        //clear
        server.delete("db",ctx -> ctx.result("{}"));
        //register
        server.post("user",ctx -> register(ctx));
        userService = new UserService();



    }

    private void register(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.body();
        var user = serializer.fromJson(reqJson, UserData.class);

        var authData = userService.register(user);

        //call to the service and register
        //current authToken creation is temporary
        ctx.result(serializer.toJson(authData));

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
