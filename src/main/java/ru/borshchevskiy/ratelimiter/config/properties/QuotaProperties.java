package ru.borshchevskiy.ratelimiter.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QuotaProperties {

    @Bean
    @ConfigurationProperties(prefix = "rps-quotas")
    public Map<String, Double> getRpsQuotas() {
        return new HashMap<>();
    }
}
