package ru.borshchevskiy.ratelimiter.service.quota;

public interface QuotaService {
    void consumeQuota(String quotaRequest);
}
