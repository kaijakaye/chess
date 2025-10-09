package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        //clear
        server.delete("db",ctx -> ctx.result("{}"));
        //register
        server.post("user",ctx -> register(ctx));


    }

    private void register(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        //call to the service and register
        //current authToken creation is temporary
        var response = Map.of("username",req.get("username"),"authToken","yzx");
        ctx.result(serializer.toJson(response));

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
