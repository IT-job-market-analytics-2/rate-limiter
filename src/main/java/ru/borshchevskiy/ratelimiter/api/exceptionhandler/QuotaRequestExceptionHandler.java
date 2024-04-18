package ru.borshchevskiy.ratelimiter.api.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.borshchevskiy.ratelimiter.api.controller.QuotaController;
import ru.borshchevskiy.ratelimiter.exception.BadRequestException;
import ru.borshchevskiy.ratelimiter.exception.NotFoundException;

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
}
