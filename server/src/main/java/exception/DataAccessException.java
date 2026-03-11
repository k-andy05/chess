package exception;

import javax.xml.crypto.Data;

public class DataAccessException extends RuntimeException {
    private String status;

    public DataAccessException(String status, String message) {
        super(message);
        this.status = status;
    }

    public String getStatus() { return status; }

    public DataAccessException(String message) {
        super(message); // TODO need to add second arg ex from in DatabaseManager class
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
