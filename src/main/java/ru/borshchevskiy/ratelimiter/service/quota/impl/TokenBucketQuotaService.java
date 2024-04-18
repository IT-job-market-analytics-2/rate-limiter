package ru.borshchevskiy.ratelimiter.service.quota.impl;

import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.borshchevskiy.ratelimiter.exception.NotFoundException;
import ru.borshchevskiy.ratelimiter.exception.QuotaAllocationException;
import ru.borshchevskiy.ratelimiter.service.quota.QuotaService;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBucketQuotaService implements QuotaService {

    @Value("${quota.response.max-wait-time:10000}")
    private long maxWaitTimeMillis;
    private final Map<String, Long> rpsQuotas;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketQuotaService(Map<String, Long> rpsQuotas) {
        this.rpsQuotas = rpsQuotas;
    }

    @PostConstruct
    private void initBuckets() {
        for (var entry : rpsQuotas.entrySet()) {
            String operationId = entry.getKey();
            Long rps = entry.getValue();
            Bucket bucket = Bucket.builder()
                    .addLimit(limit -> limit.capacity(rps)
                            .refillGreedy(rps, Duration.ofSeconds(1L)))
                    .build();
            buckets.put(operationId, bucket);
        }
    }

    public void consumeQuota(String operationId) {
        checkRequest(operationId);
        Bucket bucket = buckets.get(operationId);
        boolean isQuotaGranted;
        try {
            isQuotaGranted = bucket.asBlocking()
                    .tryConsume(1L, Duration.ofMillis(maxWaitTimeMillis), BlockingStrategy.PARKING);
        } catch (InterruptedException exception) {
            throw new QuotaAllocationException("Failed to provide quota for request " + operationId +
                    " - thread was interrupted.");
        }
        if (!isQuotaGranted) {
            throw new QuotaAllocationException("Failed to provide quota for request " + operationId +
                    " - maximum wait time of " + maxWaitTimeMillis + " (milliseconds) was exceeded.");
        }
    }

    private void checkRequest(String operationId) {
        if (!rpsQuotas.containsKey(operationId)) {
            throw new NotFoundException("Requested operation id " + operationId + " not supported.");
        }
    }
}
