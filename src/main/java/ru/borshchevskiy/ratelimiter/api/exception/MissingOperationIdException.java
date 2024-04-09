package ru.borshchevskiy.ratelimiter.api.exception;

public class MissingOperationIdException extends RuntimeException {
    public MissingOperationIdException() {
        super();
    }

    public MissingOperationIdException(String message) {
        super(message);
    }

    public MissingOperationIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingOperationIdException(Throwable cause) {
        super(cause);
    }

    protected MissingOperationIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
