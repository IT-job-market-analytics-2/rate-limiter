package ru.borshchevskiy.ratelimiter.unittests.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import ru.borshchevskiy.ratelimiter.api.controller.QuotaController;
import ru.borshchevskiy.ratelimiter.exception.NotFoundException;
import ru.borshchevskiy.ratelimiter.service.quota.QuotaService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuotaController.class)
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class QuotaControllerTest {

    private final MockMvc mockMvc;
    @MockBean
    private QuotaService tokenBucketQuotaService;

    public QuotaControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("Test GET request with valid data and expect status 200")
    public void shouldReturn200() throws Exception {
        String validRequest = "valid_request";
        doNothing().when(tokenBucketQuotaService).consumeQuotaRequest(validRequest);
        this.mockMvc.perform(get("/quota/" + validRequest))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test GET request with id that is not supported and expect status 404")
    public void shouldReturn404() throws Exception {
        String notSupportedId = "not_supported_id";
        doThrow(new NotFoundException()).when(tokenBucketQuotaService).consumeQuotaRequest(notSupportedId);
        this.mockMvc.perform(get("/quota/" + notSupportedId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test GET request without id and expect status 400")
    public void shouldReturn400() throws Exception {
        this.mockMvc.perform(get("/quota/"))
                .andExpect(status().isBadRequest());
    }
}