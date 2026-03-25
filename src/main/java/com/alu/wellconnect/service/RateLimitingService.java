package com.alu.wellconnect.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final ProxyManager<byte[]> proxyManager;

    public Bucket resolveBucket(String key, int capacity, int tokens, long seconds) {
        Supplier<BucketConfiguration> configSupplier = () -> {
            Refill refill = Refill.greedy(tokens, Duration.ofSeconds(seconds));
            Bandwidth limit = Bandwidth.classic(capacity, refill);
            return BucketConfiguration.builder()
                    .addLimit(limit)
                    .build();
        };

        return proxyManager.builder().build(key.getBytes(), configSupplier);
    }
}
