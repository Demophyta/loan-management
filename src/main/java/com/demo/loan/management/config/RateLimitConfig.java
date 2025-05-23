package com.demo.loan.management.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    @Bean
    public Map<String, Bucket> roleBasedBuckets() {
        bucketCache.put("ADMIN", createBucket(20));
        bucketCache.put("USER", createBucket(10));
        return bucketCache;
    }

    private Bucket createBucket(int capacity) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(capacity, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    public Bucket getBucketForRole(String role) {
        return bucketCache.getOrDefault(role, bucketCache.get("USER"));
    }
}
