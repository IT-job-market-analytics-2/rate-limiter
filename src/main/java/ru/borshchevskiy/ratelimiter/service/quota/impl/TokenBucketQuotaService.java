package ru.borshchevskiy.ratelimiter.service.quota.impl;

import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.borshchevskiy.ratelimiter.exception.NotFoundException;
import ru.borshchevskiy.ratelimiter.exception.QuotaAllocationException;
import ru.borshchevskiy.ratelimiter.exception.QuotaRequestTimedOutException;
import ru.borshchevskiy.ratelimiter.service.quota.QuotaService;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class provides quotas based on Token Bucket algorithm, which allows throttling
 * of incoming quota requests. Class depends on Bucket4J's {@link Bucket Bucket} implementation.
 */
@Service
public class TokenBucketQuotaService implements QuotaService {

    /**
     * Maximum time in milliseconds that thread waits  to get quota from bucket.
     */
    @Value("${quota.response.max-wait-time:10000}")
    private long maxWaitTimeMillis;

    /**
     * Map representing id of operation and allowed quota in requests per second.
     */
    private final Map<String, Long> rpsQuotas;

    /**
     * Map representing id of operation and Token Bucket which limits the request rate
     * for this operation.
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketQuotaService(Map<String, Long> rpsQuotas) {
        this.rpsQuotas = rpsQuotas;
    }
    private static final Logger log = LoggerFactory.getLogger(TokenBucketQuotaService.class);

    /**
     * Method creates and initializes token buckets for every quota
     * and putting them in {@link TokenBucketQuotaService#buckets}.
     * The {@link io.github.bucket4j.BandwidthBuilder.BandwidthBuilderRefillStage#refillGreedy(long, Duration)}
     * method refills bucket greedily, regenerating tokens as soon as they are available,
     * not waiting for time limit to regenerate a bunch of tokens at once.
     * @see Bucket
     */
    @PostConstruct
    private void initBuckets() {
        log.debug("Initializing buckets for operations - {}.", rpsQuotas.keySet());
        for (var entry : rpsQuotas.entrySet()) {
            String operationId = entry.getKey();
            Long rps = entry.getValue();
            Bucket bucket = Bucket.builder()
                    .addLimit(limit -> limit.capacity(rps)
                            .refillGreedy(rps, Duration.ofSeconds(1L)))
                    .build();
            buckets.put(operationId, bucket);
            log.debug("Initialized bucket for operation {} with {} tokens.", operationId, rps);
        }
        log.debug("Buckets for operations {} initialized successfully.", rpsQuotas.keySet());
    }

    /**
     * Method consumes quota request <code>operationId</code>, representing by id of required operation.
     * <p>If requested <code>operationId</code> is presented within available quotas it tries
     * to acquire a quota from corresponding bucket. Quota allocation algorithm:
     * <ul>
     * <li>If quota can be obtained within the time period of {@link TokenBucketQuotaService#maxWaitTimeMillis},
     * then <code>isQuotaGranted</code> set to <code>true</code> and method returns immediately.</li>
     * <li>If quota can not be obtained within the time period of {@link TokenBucketQuotaService#maxWaitTimeMillis},
     * then <code>isQuotaGranted</code> stays set to <code>false</code> and {@link QuotaRequestTimedOutException}
     * is thrown.</li>
     * <li>In case when thread waiting for quota is interrupted
     * {@link io.github.bucket4j.BlockingBucket#tryConsume(long, Duration, BlockingStrategy)} throws
     * {@link InterruptedException}, which is wrapped in {@link QuotaAllocationException}.</li>
     * </ul>
     *
     * </p>
     * @param operationId id of requested operation.
     * @throws QuotaAllocationException when quota was not provided due to thread interruption.
     * @throws QuotaRequestTimedOutException when quota was not provided due to time out.
     */
    public void consumeQuotaRequest(String operationId) {
        checkRequest(operationId);
        log.debug("Request operationId is found, trying to get quota.");
        Bucket bucket = buckets.get(operationId);
        boolean isQuotaGranted;
        try {
            isQuotaGranted = bucket.asBlocking()
                    .tryConsume(1L, Duration.ofMillis(maxWaitTimeMillis), BlockingStrategy.PARKING);
        } catch (InterruptedException exception) {
            log.error("Failed to provide quota for operationId " + operationId +
                    " because thread awaiting was interrupted.", exception);
            throw new QuotaAllocationException("Failed to provide quota for request " + operationId +
                    "from bucket " + bucket + ". Reason - thread was interrupted.", exception);
        }
        if (!isQuotaGranted) {
            log.debug("Failed to provide quota for operayionId {} - too many requests.", operationId);
            throw new QuotaRequestTimedOutException("Failed to provide quota for request " + operationId +
                    "from bucket " + bucket +
                    ". Reason - maximum wait time of " + maxWaitTimeMillis + " (milliseconds) was exceeded.");
        }
    }

    private void checkRequest(String operationId) {
        if (!rpsQuotas.containsKey(operationId)) {
            log.debug("Requested operationId {} is not supported.", operationId);
            throw new NotFoundException("Requested operation id " + operationId + " not supported.");
        }
    }
}
