package websocket.messages;

public class ErrorMessage extends ServerMessage {

    private String errorMsg;
    public ErrorMessage(String errorMsg) {
        super(ServerMessageType.ERROR);
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
