package ru.borshchevskiy.ratelimiter.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.ratelimiter.api.exception.MissingOperationIdException;
import ru.borshchevskiy.ratelimiter.api.exception.OperationIdNotFoundException;

import java.util.Optional;

@RestController
public class QuotaController {

    /**
     * Method handles quota requests for different operations.
     * Multiple path mappings are required to cover situations when path segment {operation_id} is not present.
     * In this case request is considered incorrectly formed and
     * a new {@link MissingOperationIdException MissingOperationIdException} is thrown.
     * @param operationId id of operation for which quota is requested.
     * @exception MissingOperationIdException if path segment {operation_id} is missing.
     */
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
