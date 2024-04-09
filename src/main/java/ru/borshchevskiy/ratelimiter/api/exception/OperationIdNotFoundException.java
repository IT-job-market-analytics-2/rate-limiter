package ru.borshchevskiy.ratelimiter.api.exception;

public class OperationIdNotFoundException extends RuntimeException{
    public OperationIdNotFoundException() {
        super();
    }

    public OperationIdNotFoundException(String message) {
        super(message);
    }

    public OperationIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationIdNotFoundException(Throwable cause) {
        super(cause);
    }

    protected OperationIdNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
