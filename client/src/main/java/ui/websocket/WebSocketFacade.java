package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler msgHandler;

    public WebSocketFacade(String url, ServerMessageHandler msgHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.msgHandler = msgHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message){
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    var type = notification.getServerMessageType();
                    if(type==ServerMessage.ServerMessageType.LOAD_GAME){
                        LoadGameMessage notif = new Gson().fromJson(message, LoadGameMessage.class);
                        msgHandler.notifyLoadGame(notif);
                    }
                    else if(type==ServerMessage.ServerMessageType.ERROR){
                        ErrorMessage notif = new Gson().fromJson(message, ErrorMessage.class);
                        msgHandler.notifyError(notif);
                    }
                    else{
                        NotificationMessage notif = new Gson().fromJson(message, NotificationMessage.class);
                        msgHandler.notifyNotification(notif);
                    }

                }
            });
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        try {
            var command = new MakeMoveCommand(MakeMoveCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

}
