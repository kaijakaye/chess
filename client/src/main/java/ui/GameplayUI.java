package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.ListGamesResult;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private final ServerFacade server;
    private State state = State.SIGNEDIN;
    private GameState gameState = GameState.JOINED;
    private ChessGame.TeamColor color;

    public GameplayUI(String serverUrl,ChessGame.TeamColor color) throws Exception {
        server = new ServerFacade(serverUrl);
        this.color = color;
    }

    public void run() {
        System.out.println("\n\nFinally, a real game!");
        printGameBoard(color);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(true) {
            if(gameState==GameState.NOTJOINED){
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
                //case "leave" -> leave();
                //case "redraw" -> redraw();
                //case "move" -> makeMove(params);
                //case "resign" -> resign();
                //case "highlight" -> highlightLegalMoves(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
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

    void printGameBoard(ChessGame.TeamColor who){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawBoard(out, who);
    }

    private static void drawBoard(PrintStream out, ChessGame.TeamColor who) {
        boolean q1st;
        if(who== ChessGame.TeamColor.WHITE){
            q1st = true;
        }
        else{
            q1st = false;
        }

        String[] indices;
        if(who==ChessGame.TeamColor.WHITE){
            indices = new String[]{" 8 "," 7 "," 6 "," 5 "," 4 "," 3 "," 2 "," 1 "};
        }
        else{
            indices = new String[]{" 1 "," 2 "," 3 "," 4 "," 5 "," 6 "," 7 "," 8 "};
        }

        printHorizontalIndex(out,who);
        out.print(indices[0]);
        drawLoadedRowOfSquares(out,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,who,q1st,true);
        out.print(indices[0] + "\n");
        out.print(indices[1]);
        drawRowOfPawns(out,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,who,true);
        out.print(indices[1] + "\n");
        out.print(indices[2]);
        drawEmptyRow(out,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY);
        out.print(indices[2] + "\n");
        out.print(indices[3]);
        drawEmptyRow(out,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY);
        out.print(indices[3] + "\n");
        out.print(indices[4]);
        drawEmptyRow(out,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY);
        out.print(indices[4] + "\n");
        out.print(indices[5]);
        drawEmptyRow(out,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY);
        out.print(indices[5] + "\n");
        out.print(indices[6]);
        drawRowOfPawns(out,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,who,false);
        out.print(indices[6] + "\n");
        out.print(indices[7]);
        drawLoadedRowOfSquares(out,SET_BG_COLOR_DARK_GREY,SET_TEXT_COLOR_DARK_GREY,SET_BG_COLOR_LIGHT_GREY,SET_TEXT_COLOR_LIGHT_GREY,who,q1st,false);
        out.print(indices[7] + "\n");
        printHorizontalIndex(out,who);
    }

    private static void printHorizontalIndex(PrintStream out, ChessGame.TeamColor who) {
        String[] indices;
        if(who==ChessGame.TeamColor.WHITE){
            indices = new String[]{"a","b","c","d","e","f","g","h"};
        }
        else{
            indices = new String[]{"h","g","f","e","d","c","b","a"};
        }

        out.print("   ");

        for(int sqCounter = 0; sqCounter < 8; sqCounter++){
            out.print("  " + indices[sqCounter] + "  ");
        }
        out.print("\n");
    }

    private static void drawLoadedRowOfSquares(PrintStream out,
                                               String firstBG, String firstTxt,
                                               String secondBG, String secondTxt,
                                               ChessGame.TeamColor who, boolean queenFirst, boolean isEnemy) {

        String pieceColor;
        if(!isEnemy){
            pieceColor = (who == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        }
        else{
            pieceColor = (who == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_WHITE;
        }

        String[] pieces = {
                ROOK,
                KNIGHT,
                BISHOP,
                queenFirst ? QUEEN : KING,
                queenFirst ? KING : QUEEN,
                BISHOP,
                KNIGHT,
                ROOK
        };

        for (int i = 0; i < 8; i++) {
            boolean first = (i % 2 == 0);

            if (first){
                setColor(out, firstBG, firstTxt);
            }
            else{
                setColor(out, secondBG, secondTxt);
            }

            out.print(" ");
            out.print(pieceColor);
            out.print(pieces[i]);

            if (first){
                setColor(out, firstBG, firstTxt);
            }
            else{
                setColor(out, secondBG, secondTxt);
            }

            out.print(" ");
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawRowOfPawns(PrintStream out,
                                       String firstBG, String firstTxt,
                                       String secondBG, String secondTxt,
                                       ChessGame.TeamColor who, boolean isEnemy) {

        String pieceColor;
        if(!isEnemy){
            pieceColor = (who == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        }
        else{
            pieceColor = (who == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_WHITE;
        }

        for (int i = 0; i < 8; i++) {
            boolean first = (i % 2 == 0);

            if (first){
                setColor(out, firstBG, firstTxt);
            }
            else{
                setColor(out, secondBG, secondTxt);
            }

            out.print(" ");
            out.print(pieceColor);
            out.print(PAWN);

            if (first){
                setColor(out, firstBG, firstTxt);
            }
            else{
                setColor(out, secondBG, secondTxt);
            }

            out.print(" ");
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawEmptyRow(PrintStream out,
                                     String firstBG, String firstTxt,
                                     String secondBG, String secondTxt) {

        for (int i = 0; i < 8; i++) {
            boolean first = (i % 2 == 0);

            if (first){
                setColor(out, firstBG, firstTxt);
            }
            else{
                setColor(out, secondBG, secondTxt);
            }

            out.print("     ");
        }
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void setColor(PrintStream out, String bg, String txt) {
        out.print(bg);
        out.print(txt);
    }
}
