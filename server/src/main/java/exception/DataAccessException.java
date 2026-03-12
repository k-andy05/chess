package exception;

import io.javalin.http.HttpStatus;

public class DataAccessException extends RuntimeException {
    private String status;

    public DataAccessException(String status, String message) {
        super(message);
        this.status = status;
    }

//    public HttpStatus getStatus() { return status; }

    public DataAccessException(String message) {
        super(message);
    }
}
//
//public class InvalidRequestException extends RuntimeException {
//    private final int statusCode;
//
//    public InvalidRequestException(int statusCode, String message) {
//        super(message);
//        this.statusCode = statusCode;
//    }
//
//    public int getStatusCode() {
//        return statusCode;
//    }
//}
