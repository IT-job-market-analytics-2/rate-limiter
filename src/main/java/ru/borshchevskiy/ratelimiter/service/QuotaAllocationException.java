package ru.borshchevskiy.ratelimiter.service;

public class QuotaAllocationException extends RuntimeException{
    public QuotaAllocationException() {
    }

    public QuotaAllocationException(String message) {
        super(message);
    }

    public QuotaAllocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuotaAllocationException(Throwable cause) {
        super(cause);
    }

    public QuotaAllocationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
