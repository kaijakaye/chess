package server.websocket;

import chess.ChessGame;
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
        try{
            //UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            var id = command.getGameID();
            ChessGame.TeamColor color;
            var gameInfo = userService.getDataAccess().getGame(id);
            var authInfo = userService.getDataAccess().getAuth(command.getAuthToken());
            if(authInfo.username().equals(gameInfo.getWhiteUsername())){
                color = ChessGame.TeamColor.WHITE;
            }
            else if(authInfo.username().equals(gameInfo.getBlackUsername())){
                color = ChessGame.TeamColor.BLACK;
            }
            else{
                color = null;
            }

            if(gameInfo==null){
                throw new DataAccessException("not enough info");
            }
            if(authInfo==null){
                throw new DataAccessException("bad authtoken");
            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, color, ctx.session);
                //case MAKE_MOVE -> exit(command.visitorName(), ctx.session);
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
