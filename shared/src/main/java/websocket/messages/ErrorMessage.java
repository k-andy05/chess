package websocket.messages;

public class ErrorMessage extends ServerMessage {

    private String errorMessage;
    public ErrorMessage(String errorMsg) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMsg;
    }

    public String getErrorMsg() {
        return errorMessage;
    }
}
