import chess.*;
import model.AuthData;
import ui.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            PreLoginUI preLogin = new PreLoginUI(serverUrl);
            preLogin.run();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

        System.out.println("Thanks for playing!");


    }
}