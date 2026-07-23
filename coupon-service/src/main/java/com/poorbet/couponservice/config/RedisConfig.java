package com.poorbet.couponservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.poorbet.couponservice.dto.RankingResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper
    ) {
        Duration ttl = Duration.ofMinutes(20);

        return RedisCacheManager.builder(factory)
                .withCacheConfiguration(
                        "ranking-total-odds",
                        jsonCache(redisObjectMapper, RankingResponseDto.class, ttl)
                )
                .withCacheConfiguration(
                        "ranking-payout",
                        jsonCache(redisObjectMapper, RankingResponseDto.class, ttl)
                )
                .build();
    }

    private <T> RedisCacheConfiguration jsonCache(
            ObjectMapper mapper,
            Class<T> type,
            Duration ttl
    ) {
        Jackson2JsonRedisSerializer<T> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, type);

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                )
                .entryTtl(ttl)
                .disableCachingNullValues();
    }
}
