package ui;

import chess.ChessGame;
import model.*;

import javax.management.Notification;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiConsumer;

import static ui.EscapeSequences.*;

public class PostLoginUI {
    private final ServerFacade server;
    private State state = State.SIGNEDIN;
    private AuthData auth;

    //FOR THE BOARD
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 8;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

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
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
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

    public String joinGame(String... params) throws Exception {
        if (params.length == 2) {
            int gameID;
            try {
                gameID = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                return "Join via game number, not game name";
            }
            JoinGameRequest gameReq;
            //checking validity of white or black stuff
            if(Objects.equals(params[1], "white")||Objects.equals(params[1], "WHITE")){
                gameReq = new JoinGameRequest(ChessGame.TeamColor.WHITE,gameID);
            }
            else if(Objects.equals(params[1], "black")||Objects.equals(params[1], "BLACK")){
                gameReq = new JoinGameRequest(ChessGame.TeamColor.BLACK,gameID);
            }
            else{
                return "You have to join as white or black.";
            }
            try{
                server.joinGame(auth,gameReq);
                return String.format("Successfully joined game %d as %s player.", gameID, params[1]);
            }
            catch (Exception e) {
                return String.format("Joining game %d was unsuccessful.", gameID);
            }
        }
        throw new Exception("Invalid input");
    }

    public String observeGame(String... params) throws Exception {
        printGameBoard(ChessGame.TeamColor.WHITE);
        return "I'll implement u later";
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

    public AuthData getAuth() {
        return auth;
    }

    public void setAuth(AuthData auth) {
        this.auth = auth;
    }

    void printGameBoard(ChessGame.TeamColor who){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawBoard(out, who);
    }

    private static void drawBoard(PrintStream out, ChessGame.TeamColor who) {
        drawLoadedRowOfSquares(out,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,who,true,true);
        drawRowOfPawns(out,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,who,true);
        for(int quickCount = 1; quickCount<=2; quickCount++){
            drawEmptyRow(out,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY);
            drawEmptyRow(out,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY);
        }
        drawRowOfPawns(out,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,who,false);
        drawLoadedRowOfSquares(out,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,who,true,false);
    }

    private static void drawLoadedRowOfSquares(PrintStream out,
                                               String firstBG, String firstTxt,
                                               String secondBG, String secondTxt,
                                               ChessGame.TeamColor who, boolean queenFirst, boolean isEnemy) {

        // Pick piece prefix (WHITE_* or BLACK_*)
        String rook   = (who == ChessGame.TeamColor.WHITE) ? WHITE_ROOK   : BLACK_ROOK;
        String knight = (who == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
        String bishop = (who == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
        String queen  = (who == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN  : BLACK_QUEEN;
        String king   = (who == ChessGame.TeamColor.WHITE) ? WHITE_KING   : BLACK_KING;

        String pieceColor;
        if(!isEnemy){
            pieceColor = (who == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        }
        else{
            pieceColor = (who == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_WHITE;
        }


        String[] pieces = {
                rook,
                knight,
                bishop,
                queenFirst ? queen : king,
                queenFirst ? king : queen,
                bishop,
                knight,
                rook
        };

        for (int i = 0; i < 8; i++) {
            boolean first = (i % 2 == 0);

            if (first) setColor(out, firstBG, firstTxt);
            else setColor(out, secondBG, secondTxt);

            out.print(" ");
            out.print(pieceColor);
            out.print(pieces[i]);

            if (first) setColor(out, firstBG, firstTxt);
            else setColor(out, secondBG, secondTxt);

            out.print(" ");
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.println();
    }

    private static void drawRowOfPawns(PrintStream out,
                                               String firstBG, String firstTxt,
                                               String secondBG, String secondTxt,
                                               ChessGame.TeamColor who, boolean isEnemy) {

        String pawn = (who == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
        String pieceColor;
        if(!isEnemy){
            pieceColor = (who == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        }
        else{
            pieceColor = (who == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_WHITE;
        }

        for (int i = 0; i < 8; i++) {
            boolean first = (i % 2 == 0);

            if (first) setColor(out, firstBG, firstTxt);
            else setColor(out, secondBG, secondTxt);

            out.print(" ");
            out.print(pieceColor);
            out.print(pawn);

            if (first) setColor(out, firstBG, firstTxt);
            else setColor(out, secondBG, secondTxt);

            out.print(" ");
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.println();
    }

    private static void drawEmptyRow(PrintStream out,
                                       String firstBG, String firstTxt,
                                       String secondBG, String secondTxt) {

        for (int i = 0; i < 8; i++) {
            boolean first = (i % 2 == 0);

            if (first) setColor(out, firstBG, firstTxt);
            else setColor(out, secondBG, secondTxt);

            out.print(" ");
            out.print(EMPTY);
            out.print(" ");
        }
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.println();
    }

    private static void setColor(PrintStream out, String bg, String txt) {
        out.print(bg);
        out.print(txt);
    }
}
