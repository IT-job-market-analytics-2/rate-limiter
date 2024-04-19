package ru.borshchevskiy.ratelimiter.unittests.service.quota.impl;

import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.borshchevskiy.ratelimiter.exception.NotFoundException;
import ru.borshchevskiy.ratelimiter.exception.QuotaAllocationException;
import ru.borshchevskiy.ratelimiter.exception.QuotaRequestTimedOutException;
import ru.borshchevskiy.ratelimiter.service.quota.impl.TokenBucketQuotaService;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBucketQuotaServiceTest {

    private final long MAX_WAIT_TIME_MILLIS = 10000L;
    private final String TEST_OPERATION_1 = "test1";
    private final String TEST_OPERATION_2 = "test2";
    private final Long TEST_RPS_1 = 30L;
    private final Long TEST_RPS_2 = 1L;
    @Mock
    private Map<String, Bucket> buckets;
    @InjectMocks
    private TokenBucketQuotaService tokenBucketQuotaService =
            new TokenBucketQuotaService(Map.of(TEST_OPERATION_1, TEST_RPS_1, TEST_OPERATION_2, TEST_RPS_2));

    @BeforeEach
    public void prepare() {
        ReflectionTestUtils.setField(tokenBucketQuotaService, "maxWaitTimeMillis", MAX_WAIT_TIME_MILLIS);
        ReflectionTestUtils.setField(tokenBucketQuotaService, "buckets", buckets);
    }

    @Test
    @DisplayName("Test service with invalid request - expect NotFoundException is thrown")
    public void testInvalidRequest() {
        String invalidRequest = "invalidRequest";
        assertThrows(NotFoundException.class, () -> tokenBucketQuotaService.consumeQuotaRequest(invalidRequest));
    }

    @Test
    @DisplayName("Test service with valid request - expect method returned")
    public void testNormalRequest() {
        when(buckets.get(TEST_OPERATION_1)).thenReturn(Bucket.builder()
                .addLimit(limit -> limit.capacity(TEST_RPS_1)
                        .refillGreedy(TEST_RPS_1, Duration.ofSeconds(1L)))
                .build());
        assertThatNoException().isThrownBy(() -> tokenBucketQuotaService.consumeQuotaRequest(TEST_OPERATION_1));
    }

    @Test
    @DisplayName("Test service with valid request but no tokens available during maximum wait time" +
            " - QuotaRequestTimedOutException thrown due to time out")
    public void testRequestTimeOut() throws InterruptedException {
        final long tokensToConsume = 1L;
        final Bucket mockBucket = Mockito.mock(Bucket.class);
        final BlockingBucket mockBlockingBucket = Mockito.mock(BlockingBucket.class);

        when(buckets.get(TEST_OPERATION_1)).thenReturn(mockBucket);
        when(mockBucket.asBlocking()).thenReturn(mockBlockingBucket);
        when(mockBlockingBucket.tryConsume(tokensToConsume, Duration.ofMillis(MAX_WAIT_TIME_MILLIS),
                BlockingStrategy.PARKING)).thenReturn(false);

        assertThrows(QuotaRequestTimedOutException.class,
                () -> tokenBucketQuotaService.consumeQuotaRequest(TEST_OPERATION_1));
    }

    @Test
    @DisplayName("Test service with valid request but thread waiting for token is interrupted" +
            " - QuotaAllocationException thrown due InterruptedException")
    public void testRequestTHreadInterrupted() throws InterruptedException {
        final long tokensToConsume = 1L;
        final Bucket mockBucket = Mockito.mock(Bucket.class);
        final BlockingBucket mockBlockingBucket = Mockito.mock(BlockingBucket.class);

        when(buckets.get(TEST_OPERATION_1)).thenReturn(mockBucket);
        when(mockBucket.asBlocking()).thenReturn(mockBlockingBucket);
        when(mockBlockingBucket.tryConsume(tokensToConsume, Duration.ofMillis(MAX_WAIT_TIME_MILLIS),
                BlockingStrategy.PARKING)).thenThrow(new InterruptedException());

        assertThrows(QuotaAllocationException.class,
                () -> tokenBucketQuotaService.consumeQuotaRequest(TEST_OPERATION_1));
    }
}