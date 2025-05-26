package id.ac.ui.cs.advprog.orderservice.exception;

public class MenuServiceException extends RuntimeException {
    public MenuServiceException(String message) {
        super(message);
    }
    
    public MenuServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 