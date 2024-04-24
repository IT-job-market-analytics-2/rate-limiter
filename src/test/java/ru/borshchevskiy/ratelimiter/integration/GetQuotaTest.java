package ru.borshchevskiy.ratelimiter.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.borshchevskiy.ratelimiter.config.properties.QuotaProperties;
import ru.borshchevskiy.ratelimiter.exception.QuotaAllocationException;
import ru.borshchevskiy.ratelimiter.exception.QuotaRequestTimedOutException;
import ru.borshchevskiy.ratelimiter.service.quota.impl.TokenBucketQuotaService;

import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetQuotaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuotaProperties quotaProperties;

    @SpyBean
    private TokenBucketQuotaService tokenBucketQuotaService;

    @Test
    @DisplayName("Request quota with correct operationId - expect response status 200")
    public void getQuota200() throws Exception {
        Set<String> operations = quotaProperties.getRpsQuotas().keySet();
        for (String operation : operations) {
            mockMvc.perform(get("/quota/" + operation))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @Test
    @DisplayName("Request quota with incorrect operationId - expect response status 404")
    public void getQuota404() throws Exception {
        String incorrectRequest = "incorrectRequest";
        mockMvc.perform(get("/quota/" + incorrectRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Request with incorrect format (without operationId path segment - expect response status 400")
    public void getQuota400() throws Exception {
        mockMvc.perform(get("/quota/"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request quota when service has too many request (mocked) - expect response status 429")
    public void getQuota429() throws Exception {
        String correctRequest = "correctRequest";

        doThrow(QuotaRequestTimedOutException.class).when(tokenBucketQuotaService).consumeQuotaRequest(correctRequest);

        mockMvc.perform(get("/quota/" + correctRequest))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Request quota and request gets interrupted while waiting fo quota (mocked) " +
            "- expect response status 503")
    public void getQuota503() throws Exception {
        String correctRequest = "correctRequest";

        doThrow(QuotaAllocationException.class).when(tokenBucketQuotaService).consumeQuotaRequest(correctRequest);

        mockMvc.perform(get("/quota/" + correctRequest))
                .andExpect(status().isServiceUnavailable());
    }
}
