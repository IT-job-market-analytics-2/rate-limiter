package ru.borshchevskiy.ratelimiter.unittests.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import ru.borshchevskiy.ratelimiter.api.controller.QuotaController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuotaController.class)
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class QuotaControllerTest {

    private final MockMvc mockMvc;

    public QuotaControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("Test GET request with valid data and expect status 200")
    public void shouldReturn200() throws Exception {
        this.mockMvc.perform(get("/quota/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test GET request with id that is not supported and expect status 404")
    public void shouldReturn404() throws Exception {
        String notSupportedId = "test_not_found";
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