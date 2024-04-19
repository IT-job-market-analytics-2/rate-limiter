package ru.borshchevskiy.ratelimiter.api.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.borshchevskiy.ratelimiter.api.controller.QuotaController;
import ru.borshchevskiy.ratelimiter.exception.BadRequestException;
import ru.borshchevskiy.ratelimiter.exception.NotFoundException;
import ru.borshchevskiy.ratelimiter.exception.QuotaAllocationException;
import ru.borshchevskiy.ratelimiter.exception.QuotaRequestTimedOutException;

@ControllerAdvice(assignableTypes = QuotaController.class)
public class QuotaRequestExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundException() {
    }

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleBadRequestException() {
    }

    @ExceptionHandler(value = QuotaAllocationException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void handleQuotaAllocationException() {
    }

    @ExceptionHandler(value = QuotaRequestTimedOutException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void handleQuotaRequestTimedOutException() {
    }
}
