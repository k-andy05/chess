package exception;

public class DataAccessException extends RuntimeException {

    public DataAccessException(String status, String message) {
        super(message);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
