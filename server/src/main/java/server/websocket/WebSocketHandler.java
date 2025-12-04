package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
//import exception.ResponseException;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;

    public WebSocketHandler(UserService userService) {
        this.userService = userService;
    }

    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    public void handleMessage(WsMessageContext ctx) throws IOException {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        MakeMoveCommand mmComm = null;
        if(command.getCommandType()== MAKE_MOVE){
            mmComm = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
        }
        try{
            var id = command.getGameID();
            ChessGame.TeamColor color;
            var gameInfo = userService.getDataAccess().getGame(id);
            var authInfo = userService.getDataAccess().getAuth(command.getAuthToken());

            if(authInfo==null){
                throw new DataAccessException("bad authtoken");
            }
            else if(gameInfo==null){
                throw new DataAccessException("not enough info");
            }
            else if(authInfo.username().equals(gameInfo.getWhiteUsername())){
                color = ChessGame.TeamColor.WHITE;
            }
            else if(authInfo.username().equals(gameInfo.getBlackUsername())){
                color = ChessGame.TeamColor.BLACK;
            }
            else{
                color = null;
            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, color, ctx.session);
                case MAKE_MOVE -> makeMove(mmComm, color, ctx.session);
                case LEAVE -> leave(command, color, ctx.session);
                //case RESIGN -> exit(command.visitorName(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        catch(DataAccessException e){
            var message = "Error";
            var messageToEveryone = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastToSelf(command.getGameID(), ctx.session, messageToEveryone);
        }
    }

    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, ChessGame.TeamColor color, Session session) throws IOException, DataAccessException{
        var id = command.getGameID();
        connections.add(id, session);
        var gameInfo = userService.getDataAccess().getGame(id);
        String message;
        if (ChessGame.TeamColor.WHITE.equals(color)) {
            message = String.format("%s successfully added to game %d as %s player", gameInfo.getWhiteUsername(), id, color);
        } else if (ChessGame.TeamColor.BLACK.equals(color)) {
            message = String.format("%s successfully added to game %d as %s player", gameInfo.getBlackUsername(), id, color);
        } else {
            message = String.format("Successfully added to game %d as observer", id);
        }

        var messageToSelf = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameInfo.getGame());
        var messageToWorld = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastToSelf(id, session, messageToSelf);
        connections.broadcastToOthers(id, session, messageToWorld);
    }

    private void makeMove(MakeMoveCommand command, ChessGame.TeamColor color, Session session) throws IOException, DataAccessException{
        var id = command.getGameID();
        var gameInfo = userService.getDataAccess().getGame(id);
        var game = gameInfo.getGame();

        try{
            ChessMove move = command.getMove();
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();

            //prep message string
            String message;
            String workingUsername = "";
            if (ChessGame.TeamColor.WHITE.equals(color) && game.getTeamTurn().equals(color)) {
                workingUsername = gameInfo.getWhiteUsername();
            } else if (ChessGame.TeamColor.BLACK.equals(color) && game.getTeamTurn().equals(color)) {
                workingUsername = gameInfo.getBlackUsername();
            } else {
                throw new InvalidMoveException();
            }
            game.makeMove(move);
            gameInfo.setGame(game);
            userService.getDataAccess().updateGame(gameInfo);
            message = String.format("%s moved from %s to %s", workingUsername, start, end);

            //send updated game and message about move to everyone involved
            var updateGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameInfo.getGame());
            var notifAboutMove = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
            connections.broadcastToAll(command.getGameID(), session, updateGame);
            connections.broadcastToOthers(command.getGameID(), session, notifAboutMove);

            //send notif about check or stalemate or whateva
            String anotherMsg = "";
            boolean didSmthFrHappen = false;
            if(game.isInCheck(color)){
                anotherMsg = String.format("%s is now in check", workingUsername);
                didSmthFrHappen = true;
            }
            else if(game.isInCheckmate(color)){
                anotherMsg = String.format("%s is checkmated. Game over!!", workingUsername);
                didSmthFrHappen = true;
            }
            else if(game.isInStalemate(color)){
                anotherMsg = "Game in stalemate. Uh ohhhhh";
                didSmthFrHappen = true;
            }
            else{
                var doNothing = 0;
            }

            if(didSmthFrHappen){
                var notifAboutGameState = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,anotherMsg);
                connections.broadcastToAll(command.getGameID(), session, notifAboutGameState);
            }

        }
        catch(InvalidMoveException e){
            var message = "Error";
            var messageToSelf = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastToSelf(command.getGameID(), session, messageToSelf);
        }
    }

    /*public static ChessMove parseUserMove(String input) {
        String[] parts = input.trim().split("\\s+"); // splits on spaces
        if (parts.length != 2) {
            throw new IllegalArgumentException("Input must be like: e2 e4");
        }

        ChessPosition start = parsePosition(parts[0]);
        ChessPosition end = parsePosition(parts[1]);

        return new ChessMove(start, end, null);
    }

    private static ChessPosition parsePosition(String pos) {
        if (pos.length() != 2) {
            throw new IllegalArgumentException("Invalid square: " + pos);
        }

        char file = pos.charAt(0);
        char rank = pos.charAt(1);

        int col = file - 'a';
        int row = Character.getNumericValue(rank) - 1;

        return new ChessPosition(row, col);
    }*/



    private void leave(UserGameCommand command, ChessGame.TeamColor color, Session session) throws IOException, DataAccessException {
        var gameInfo = userService.getDataAccess().getGame(command.getGameID());
        String message;
        if (ChessGame.TeamColor.WHITE.equals(color)) {
            message = String.format("%s left game %d", gameInfo.getWhiteUsername(), command.getGameID());
            gameInfo.setWhiteUsername(null);
            userService.getDataAccess().updateGame(gameInfo);
        } else if (ChessGame.TeamColor.BLACK.equals(color)) {
            message = String.format("%s left game %d", gameInfo.getBlackUsername(), command.getGameID());
            gameInfo.setBlackUsername(null);
            userService.getDataAccess().updateGame(gameInfo);
        } else {
            message = String.format("Observer left game %d", command.getGameID());
        }
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcastToOthers(command.getGameID(), session, serverMessage);
        connections.remove(command.getGameID(), session);
    }
}
