package ru.borshchevskiy.ratelimiter.exception;

public class QuotaRequestTimedOutException extends RuntimeException {

    public QuotaRequestTimedOutException() {
    }

    public QuotaRequestTimedOutException(String message) {
        super(message);
    }

    public QuotaRequestTimedOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuotaRequestTimedOutException(Throwable cause) {
        super(cause);
    }

    public QuotaRequestTimedOutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
