package ru.borshchevskiy.ratelimiter.api.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.borshchevskiy.ratelimiter.api.controller.QuotaController;
import ru.borshchevskiy.ratelimiter.api.exception.MissingOperationIdException;
import ru.borshchevskiy.ratelimiter.api.exception.OperationIdNotFoundException;

@ControllerAdvice(assignableTypes = QuotaController.class)
public class QuotaRequestExceptionHandler {

    @ExceptionHandler(value = OperationIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleOperationIdNotFoundException() {
    }

    @ExceptionHandler(value = MissingOperationIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleMissingOperationIdException() {
    }
}
