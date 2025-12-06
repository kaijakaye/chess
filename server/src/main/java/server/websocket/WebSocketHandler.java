package server.websocket;

import chess.*;
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

            String observerUsername = "";
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
                observerUsername = authInfo.username();
            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, color, ctx.session,observerUsername);
                //case MAKE_MOVE -> makeMove(mmComm, color, ctx.session);
                case MAKE_MOVE -> {
                    if (mmComm == null) {
                        var err = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Malformed MAKE_MOVE command");
                        connections.broadcastToSelf(command.getGameID(), ctx.session, err);
                        return;
                    }
                    makeMove(mmComm, color, ctx.session);
                }
                case LEAVE -> leave(command, color, ctx.session, observerUsername);
                case RESIGN -> resign(command, color, ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        catch(DataAccessException e){
            var message = "Error: " + e.getMessage();
            var messageToEveryone = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastToSelf(command.getGameID(), ctx.session, messageToEveryone);
        }
    }

    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, ChessGame.TeamColor color, Session session, String observer) throws IOException, DataAccessException{
        var id = command.getGameID();
        connections.add(id, session);
        var gameInfo = userService.getDataAccess().getGame(id);
        String message;
        if (ChessGame.TeamColor.WHITE.equals(color)) {
            message = String.format("%s successfully added to game %d as %s player", gameInfo.getWhiteUsername(), id, color);
        } else if (ChessGame.TeamColor.BLACK.equals(color)) {
            message = String.format("%s successfully added to game %d as %s player", gameInfo.getBlackUsername(), id, color);
        } else {
            message = String.format("%s successfully added to game %d as observer", observer, id);
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
            if(game.isGameOver()){
                throw new GameOverException();
            }

            ChessMove move = command.getMove();
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            String startStr = "";
            String endStr = "";

            switch(start.getColumn()){
                case 1:
                    startStr+='a';
                    break;
                case 2:
                    startStr+='b';
                    break;
                case 3:
                    startStr+='c';
                    break;
                case 4:
                    startStr+='d';
                    break;
                case 5:
                    startStr+='e';
                    break;
                case 6:
                    startStr+='f';
                    break;
                case 7:
                    startStr+='g';
                    break;
                case 8:
                    startStr+='h';
                    break;
            }
            startStr+=start.getRow();

            switch(end.getColumn()){
                case 1:
                    endStr+='a';
                    break;
                case 2:
                    endStr+='b';
                    break;
                case 3:
                    endStr+='c';
                    break;
                case 4:
                    endStr+='d';
                    break;
                case 5:
                    endStr+='e';
                    break;
                case 6:
                    endStr+='f';
                    break;
                case 7:
                    endStr+='g';
                    break;
                case 8:
                    endStr+='h';
                    break;
            }
            endStr+=end.getRow();

            //prep message string
            String message;
            String workingUsername = "";
            String enemyUsername = "";
            if (ChessGame.TeamColor.WHITE.equals(color) && game.getTeamTurn().equals(color)) {
                workingUsername = gameInfo.getWhiteUsername();
                enemyUsername = gameInfo.getBlackUsername();
            } else if (ChessGame.TeamColor.BLACK.equals(color) && game.getTeamTurn().equals(color)) {
                workingUsername = gameInfo.getBlackUsername();
                enemyUsername = gameInfo.getWhiteUsername();
            } else {
                throw new InvalidMoveException();
            }
            game.makeMove(move);
            gameInfo.setGame(game);
            userService.getDataAccess().updateGame(gameInfo);
            message = String.format("%s moved from %s to %s", workingUsername, startStr, endStr);

            //send updated game and message about move to everyone involved
            var updateGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameInfo.getGame());
            var notifAboutMove = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
            connections.broadcastToAll(command.getGameID(), session, updateGame);
            connections.broadcastToOthers(command.getGameID(), session, notifAboutMove);

            //send notif about check or stalemate or whateva
            String anotherMsg = "";
            boolean didSmthFrHappen = false;
            ChessGame.TeamColor enemy = (color==ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

            if(game.isInCheckmate(enemy)){
                anotherMsg = String.format("%s is checkmated. Game over!!", enemyUsername);
                didSmthFrHappen = true;
            }
            else if(game.isInCheck(enemy)){
                anotherMsg = String.format("%s is now in check", enemyUsername);
                didSmthFrHappen = true;
            }

            else if(game.isInStalemate(enemy)){
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
            System.out.println(e.getMessage());
            var message = "Error: you can't do that";
            var messageToSelf = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastToSelf(command.getGameID(), session, messageToSelf);
        } catch(GameOverException e){
            System.out.println(e.getMessage());
            var message = "Error: game's over";
            var messageToSelf = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastToSelf(command.getGameID(), session, messageToSelf);
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void leave(UserGameCommand command, ChessGame.TeamColor color, Session session, String observer) throws IOException, DataAccessException {
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
            message = String.format("%s left game %d", observer, command.getGameID());
        }
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcastToOthers(command.getGameID(), session, serverMessage);
        connections.remove(command.getGameID(), session);
    }

    private void resign(UserGameCommand command, ChessGame.TeamColor color, Session session) throws IOException, DataAccessException {
        //Server marks the game as over (no more moves can be made)
        //game is updated in the database.
        try{
            //if someone's already resigned
            var id = command.getGameID();
            var gameInfo = userService.getDataAccess().getGame(id);
            var game = gameInfo.getGame();

            if(game.isGameOver()){
                throw new GameOverException();
            }
            else{
                game.setGameOver(true);
            }
            String message;
            String workingUsername = "";
            if (ChessGame.TeamColor.WHITE.equals(color)) {
                workingUsername = gameInfo.getWhiteUsername();
            } else if (ChessGame.TeamColor.BLACK.equals(color)) {
                workingUsername = gameInfo.getBlackUsername();
            } else {
                throw new GameOverException();
            }

            game.setGameOver(true);
            gameInfo.setGame(game);
            userService.getDataAccess().updateGame(gameInfo);
            //Server sends a Notification message to all clients in that game informing them that the root client resigned.

            message = String.format("%s resigned from the game. No more playing.", workingUsername);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcastToAll(command.getGameID(), session, serverMessage);
        } catch (GameOverException e) {
            var message = "Error: game's over!";
            var messageToSelf = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastToSelf(command.getGameID(), session, messageToSelf);
        }

        //This applies to both players and observers.
    }
}
