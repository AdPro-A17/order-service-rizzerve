package id.ac.ui.cs.advprog.orderservice.exception;

public class CouponApplicationException extends RuntimeException {
    public CouponApplicationException(String message) {
        super(message);
    }

    public CouponApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}