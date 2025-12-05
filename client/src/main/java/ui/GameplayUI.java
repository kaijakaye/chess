package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.websocket.ServerMessageHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static java.awt.Color.*;
import static ui.EscapeSequences.*;

public class GameplayUI implements ServerMessageHandler {
    private final WebSocketFacade ws;
    private State state = State.SIGNEDIN;
    private GameState gameState = GameState.JOINED;
    private final ChessGame.TeamColor color;
    private final int gameID;
    private ChessGame game = new ChessGame();
    private String authToken;

    public GameplayUI(String serverUrl, ChessGame.TeamColor color, int gameID, String authToken) throws Exception {
        ws = new WebSocketFacade(serverUrl, this);
        this.color = color;
        this.gameID = gameID;
        this.authToken = authToken;
    }

    public void run() {
        System.out.println("\n\nFinally, a real game!");;
        redraw(game.getBoard(),color);
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
                case "leave" -> leave();
                case "redraw" -> redraw(game.getBoard(),color);
                //case "move" -> makeMove(params);
                case "resign" -> resign();
                //case "highlight" -> highlightLegalMoves(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                leave - leave the game and return to the last menu
                redraw - redraw the game board
                make move <starting position (ex. A1)> <ending position(ex. B2)> - make a move
                resign - give up
                highlight <position> - highlight the legal moves for a piece at a given position
                help - possible commands
                """;
    }

    public String leave() throws Exception {
        gameState = GameState.NOTJOINED;
        ws.leave(authToken, gameID);
        return "You left the game successfully.";
    }

    public String resign() throws Exception {
        System.out.print("Are you sure you want to resign? ('y' for yes, 'n' for no)");
        Scanner scanner = new Scanner(System.in);
        boolean result;
        printPrompt();
        String line = scanner.nextLine();
        String[] tokens = line.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "no";
        try {
            result = evalResign(line);
            if(result){
                ws.resign(authToken, gameID);
                return "You resigned from the game.";
            }
            else{
                return "You can stay in the game then.";
            }
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(msg);
        }
        return "Resign failed.";
    }

    public boolean evalResign(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "no";
            return switch (cmd) {
                case "y" -> true;
                case "yes" -> true;
                default -> false;
            };
        } catch (Exception ex) {
            return false;
        }
    }

    public String redraw(ChessBoard board, ChessGame.TeamColor who){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String[] indices;
        if(who==ChessGame.TeamColor.WHITE){
            indices = new String[]{" 8 "," 7 "," 6 "," 5 "," 4 "," 3 "," 2 "," 1 "};
        }
        else{
            indices = new String[]{" 1 "," 2 "," 3 "," 4 "," 5 "," 6 "," 7 "," 8 "};
        }

        //print top row indices
        printHorizontalIndex(out,who);
        //for each row, start with the vertical crap
        for (int row = 1; row <= 8; row++) {
            out.print(indices[row - 1]);
            for (int col = 1; col <= 8; col++) {
                String bgColor = "";
                String txtColor = "";
                //light grey
                if((row%2==1 && col%2==1) || (row%2==0 && col%2==0)){
                    bgColor = SET_BG_COLOR_LIGHT_GREY;
                    txtColor = SET_TEXT_COLOR_LIGHT_GREY;
                }
                //dark grey
                else{
                    bgColor = SET_BG_COLOR_DARK_GREY;
                    txtColor = SET_TEXT_COLOR_DARK_GREY;
                }
                printSingleSquare(out, board.getPiece(new ChessPosition(row,col)),bgColor, txtColor, who);
            }
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.print(indices[row - 1] + "\n");
        }
        printHorizontalIndex(out,who);

        return "There's your board.";
    }

    public void printSingleSquare(PrintStream out, ChessPiece piece, String bgColor, String txtColor, ChessGame.TeamColor who){

        setColor(out,bgColor,txtColor);
        out.print(" ");

        String pieceColor = "";
        if(!(piece==null)){
            if(piece.getTeamColor()==ChessGame.TeamColor.WHITE){
                pieceColor = SET_TEXT_COLOR_WHITE;
            }
            else{
                pieceColor = SET_TEXT_COLOR_BLACK;
            }
            setColor(out,bgColor,pieceColor);
            out.print(piece);
        }
        else{
            out.print(" ");
        }

        setColor(out,bgColor,txtColor);
        out.print(" ");
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
            out.print(" " + indices[sqCounter] + " ");
        }
        out.print("\n");
    }

    @Override
    public void notifyLoadGame(LoadGameMessage msg) {
        game = msg.getGame();
        redraw(game.getBoard(),color);
        printPrompt();
    }

    @Override
    public void notifyError(ErrorMessage msg) {
        System.out.println(RED + msg.getErrorMessage());
        printPrompt();
    }

    @Override
    public void notifyNotification(NotificationMessage msg) {
        System.out.println(BLUE + msg.getMessage());
        printPrompt();
    }

    private static void setColor(PrintStream out, String bg, String txt) {
        out.print(bg);
        out.print(txt);
    }
}
