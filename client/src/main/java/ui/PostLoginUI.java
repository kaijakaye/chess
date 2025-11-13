package ui;

import model.*;

import javax.management.Notification;
import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade server;
    private State state = State.SIGNEDIN;
    private AuthData auth;

    public PostLoginUI(String serverUrl,AuthData auth) throws Exception {
        server = new ServerFacade(serverUrl);
        this.auth = auth;
    }

    public void run() {
        System.out.println("\nNow you have some more options. Sign in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(true) {
            if(state==State.SIGNEDOUT){
                return;
            }
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws Exception {
        state = State.SIGNEDOUT;
        server.logout(auth);
        auth = null;
        return "You logged out successfully.";
    }

    public String help() {
        return """
                logout - log out & return to start menu
                help - possible commands
                """;
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }

    public AuthData getAuth() {
        return auth;
    }

    public void setAuth(AuthData auth) {
        this.auth = auth;
    }
}
