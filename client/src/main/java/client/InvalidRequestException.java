package client;

public class InvalidRequestException extends RuntimeException {
    private final int statusCode;

    public InvalidRequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
