package ui;

import java.util.Arrays;
import java.util.Scanner;

import model.*;

import javax.management.Notification;

public class PreLoginUI {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    //private PostLoginUI jumpToPost;
    private AuthData auth;

    public PreLoginUI(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
    }

    public AuthData run() {
        System.out.println(" Let's play some chess. Sign in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if(state==State.SIGNEDIN){
                return auth;
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
        System.out.println();
        return null;
    }


    public void notify(Notification notification) {
        System.out.println(notification.getMessage());
        printPrompt();
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
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length == 3) {
            state = State.SIGNEDIN;
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var userToSendIn = new UserData(username,password,email);
            auth = server.register(userToSendIn);
            return String.format("You registered successfully as %s.", username);
        }
        throw new Exception("Invalid input");
    }

    public String login(String... params) throws Exception {
        if (params.length == 2) {
            state = State.SIGNEDIN;
            var username = params[0];
            var password = params[1];
            var userToSendIn = new UserData(username,password,null);
            auth = server.login(userToSendIn);
            return String.format("You logged in successfully as %s.", username);
        }
        throw new Exception("Invalid input");
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - no more playing chess
                help - possible commands
                """;
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }
}
