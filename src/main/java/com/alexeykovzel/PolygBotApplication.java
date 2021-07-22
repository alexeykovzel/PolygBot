package com.alexeykovzel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
@EnableCaching
public class PolygBotApplication {
    public static final Logger logger = LoggerFactory.getLogger(PolygBotApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PolygBotApplication.class, args);
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Collections.singletonList(
                new ConcurrentMapCache("value")));
        return cacheManager;
    }
}
