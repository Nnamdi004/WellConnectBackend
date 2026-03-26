package com.alu.wellconnect.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimitingService {

    private static final java.util.Map<String, Bucket> bucketCache = new java.util.concurrent.ConcurrentHashMap<>();

    public Bucket resolveBucket(String key, int capacity, int tokens, long seconds) {
        return bucketCache.computeIfAbsent(key, k -> {
            Refill refill = Refill.greedy(tokens, Duration.ofSeconds(seconds));
            Bandwidth limit = Bandwidth.classic(capacity, refill);
            return io.github.bucket4j.Bucket4j.builder()
                    .addLimit(limit)
                    .build();
        });
    }
}
