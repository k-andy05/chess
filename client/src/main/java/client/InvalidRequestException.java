package client;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(int statusCode, String message) {
        super(message);
    }
}
