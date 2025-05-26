package id.ac.ui.cs.advprog.orderservice.exception;

public class InvalidOrderStatusForCheckoutException extends RuntimeException {
    public InvalidOrderStatusForCheckoutException(String status) {
        super("Cannot checkout order with status: " + status + ". Order must be in NEW status.");
    }
}