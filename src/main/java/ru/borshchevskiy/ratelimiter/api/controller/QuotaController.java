package ru.borshchevskiy.ratelimiter.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.borshchevskiy.ratelimiter.exception.BadRequestException;
import ru.borshchevskiy.ratelimiter.service.quota.QuotaService;

@RestController
public class QuotaController {

    private final QuotaService tokenBucketQuotaService;
    private static final  Logger log = LoggerFactory.getLogger(QuotaController.class);

    public QuotaController(QuotaService tokenBucketQuotaService) {
        this.tokenBucketQuotaService = tokenBucketQuotaService;
    }

    @GetMapping("/quota/{operation_id}")
    @ResponseStatus(HttpStatus.OK)
    public void getQuota(@PathVariable(value = "operation_id") String operationId) {
        log.debug("Received quota request for operation {}.", operationId);
        tokenBucketQuotaService.consumeQuotaRequest(operationId);
        log.debug("Provided quota for operation {}.", operationId);
    }

    /**
     * Method handles quota requests in situations when path segment /{operation_id} is not present.
     * In this case request is considered incorrectly formed and
     * a new {@link BadRequestException BadRequestException} is thrown.
     *
     * @throws BadRequestException if path segment /operation_id} is missing.
     */
    @GetMapping(value = {"/quota", "/quota/"})
    public void getQuota() {
        log.debug("Received bad quota request.");
        throw new BadRequestException();
    }
}
