package ru.borshchevskiy.ratelimiter.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.ratelimiter.api.exception.MissingOperationIdException;
import ru.borshchevskiy.ratelimiter.api.exception.OperationIdNotFoundException;

import java.util.Optional;

@RestController
public class QuotaController {

    @GetMapping(value = {"/quota", "/quota/", "/quota/{operation_id}"})
    @ResponseStatus(HttpStatus.OK)
    public void getQuota(@PathVariable(value = "operation_id", required = true) Optional<String> operationId) {
        String id = operationId.orElseThrow(MissingOperationIdException::new);

        // TODO: test implementation, add logic
        if (id.equals("test_not_found")) {
            throw new OperationIdNotFoundException();
        }
    }
}
