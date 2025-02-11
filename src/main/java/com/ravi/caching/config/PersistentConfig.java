package com.ravi.caching.config;

import com.ravi.caching.modal.Book;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@CacheConfig
public class PersistentConfig {

    private static final String CACHE_DIR = "cache-storage"; // Define cache storage path

    @Bean
    public org.ehcache.CacheManager customCacheManager() {
        File cacheDir = new File(CACHE_DIR);

        ResourcePoolsBuilder.newResourcePoolsBuilder();
        return CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(cacheDir)) // Enable disk persistence
                .withCache("myPersistentCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Integer.class, Book.class,
                                ResourcePoolsBuilder
                                        .heap(100) // Store 100 objects in heap
                                        .disk(50, MemoryUnit.MB, true) // Store 50MB in disk
                        )
                )
                .build(true);
    }
}

