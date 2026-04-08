package websocket.messages;

public class ErrorMessage extends ServerMessage {

    private String errorMsg;
    public ErrorMessage(ServerMessageType type, String errorMsg) {
        super(type);
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
