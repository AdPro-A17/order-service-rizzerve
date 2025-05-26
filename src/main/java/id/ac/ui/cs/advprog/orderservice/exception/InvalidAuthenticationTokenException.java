package id.ac.ui.cs.advprog.orderservice.exception;
 
public class InvalidAuthenticationTokenException extends RuntimeException {
    public InvalidAuthenticationTokenException(String message) {
        super(message);
    }
} 