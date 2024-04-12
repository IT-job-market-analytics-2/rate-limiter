package ru.borshchevskiy.ratelimiter.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.ratelimiter.api.exception.BadRequestException;
import ru.borshchevskiy.ratelimiter.api.exception.NotFoundException;

@RestController
public class QuotaController {

    @GetMapping("/quota/{operation_id}")
    @ResponseStatus(HttpStatus.OK)
    public void getQuota(@PathVariable(value = "operation_id") String operationId) {
        // TODO: test implementation, add logic
        if (operationId.equals("test_not_found")) {
            throw new NotFoundException();
        }
    }

    /**
     * Method handles quota requests in situations when path segment /{operation_id} is not present.
     * In this case request is considered incorrectly formed and
     * a new {@link BadRequestException BadRequestException} is thrown.
     * @exception BadRequestException if path segment /operation_id} is missing.
     */
    @GetMapping(value = {"/quota", "/quota/"})
    public void getQuota() {
        throw new BadRequestException();
    }
}
