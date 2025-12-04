package ui.websocket;

import websocket.messages.*;

public interface ServerMessageHandler {
    void notifyLoadGame(LoadGameMessage msg);
    void notifyError(ErrorMessage msg);
    void notifyNotification(NotificationMessage msg);
}