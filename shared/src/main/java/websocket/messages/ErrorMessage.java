package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;

    public ErrorMessage(ServerMessage.ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
