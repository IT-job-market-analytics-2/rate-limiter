package ru.borshchevskiy.ratelimiter.config.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("ide")
class QuotaPropertiesTest {

    @Autowired
    private Map<String, Long> rpsQuotas;

    @Test
    @DisplayName("Test that properties are read from .yml file and values are properly mapped")
    public void ensurePropertiesSet() {
        assertNotNull(rpsQuotas);
        assertAll(
                () ->  assertTrue(rpsQuotas.containsKey("hh-api")),
                () ->  assertTrue(rpsQuotas.containsKey("telegram-api")),
                () ->  assertTrue(rpsQuotas.containsKey("test"))
        );
        assertAll(
                () ->  assertEquals(30, rpsQuotas.get("hh-api")),
                () ->  assertEquals(1, rpsQuotas.get("telegram-api")),
                () ->  assertEquals(1, rpsQuotas.get("test"))
        );
    }
}