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
        System.out.println("\n\nNow you have some more options.");
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
                case "create" -> createGame(params);
                case "list" -> listGames();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws Exception {
        if (params.length == 1) {
            var gameName = params[0];
            var gameToSendIn = new GameData(gameName);
            try{
                var returnedGame = server.createGame(auth,gameToSendIn);
                return String.format("Successfully created game %s.", gameName);
            }
            catch (Exception e) {
                return String.format("Creating game %s was unsuccessful.", gameName);
            }
        }
        throw new Exception("Invalid input");
    }

    public String listGames() throws Exception {
        ListGamesResult result = server.listGames(auth);
        int counter = 1;
        for(GameData game : result.games()){
            System.out.printf("%d.  %s  whiteUser: %s   blackUser: %s\n",
                    counter, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
            ++counter;
        }
        return "\nThat's the list!\n";
    }

    public String logout() throws Exception {
        state = State.SIGNEDOUT;
        server.logout(auth);
        auth = null;
        return "You logged out successfully.";
    }

    public String help() {
        return """
                create <NAME> - create a new game called NAME
                list - see the list of all games
                join <ID> [WHITE|BLACK] - join an existing game
                observe <ID> - watch a game
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
