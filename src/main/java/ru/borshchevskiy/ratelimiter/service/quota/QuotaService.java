package ru.borshchevskiy.ratelimiter.service.quota;

public interface QuotaService {
    void consumeQuotaRequest(String quotaRequest);
}
