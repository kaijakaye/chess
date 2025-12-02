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

    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), command.getColor(), ctx.session);
                //case MAKE_MOVE -> exit(command.visitorName(), ctx.session);
                //case LEAVE -> exit(command.visitorName(), ctx.session);
                //case RESIGN -> exit(command.visitorName(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(int id, ChessGame.TeamColor color, Session session) throws IOException, DataAccessException {
        connections.add(id, session);
        var gameInfo = userService.getDataAccess().getGame(id);
        String message;
        if(color==ChessGame.TeamColor.WHITE){
            message = String.format("%s successfully added to game %d as %s user", gameInfo.getWhiteUsername(), id, color);
        }
        else{
            message = String.format("%s successfully added to game %d as %s user", gameInfo.getBlackUsername(), id, color);
        }
        var messageToSelf = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameInfo.getGame());
        var messageToWorld = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastToSelf(id, session, messageToSelf);
        connections.broadcastToOthers(id, session, messageToWorld);
    }

    private void leave(int id, Session session) throws IOException {
        var message = String.format("User left game %d", id);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        //connections.broadcast(id, session, serverMessage);
        connections.remove(id, session);
    }
}
