package com.ravi.caching.config;

import com.ravi.caching.modal.Book;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
public class EhcacheConfig {

    public static final String CACHE_STORE_NAME = "cacheStore";

    @Bean
    public CacheManager ehcacheManager() {
        org.ehcache.config.CacheConfiguration<Integer, Book> cacheConfig = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class,
                        Book.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .disk(10, MemoryUnit.MB)
                                .offheap(2, MemoryUnit.MB)
                                .build())
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(10)))
                .build();

        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();

        javax.cache.configuration.Configuration<Integer, Book> configuration = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfig);
        cacheManager.createCache(CACHE_STORE_NAME, configuration);
        return cacheManager;
    }
}
