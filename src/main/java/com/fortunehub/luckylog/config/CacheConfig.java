package com.fortunehub.luckylog.config;

import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

  @Bean(name = "fortuneResultCache")
  public Cache<String, List<FortuneResponse>> fortuneCache() {
    return Caffeine.newBuilder()
                   .expireAfterWrite(1, TimeUnit.DAYS)
                   .maximumSize(300)
                   .build();
  }
}
