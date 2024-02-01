package org.etsntesla.gava.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;


public class CustomRedisTemplate extends RedisTemplate<String, Object> {

    public CustomRedisTemplate() {
        setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        setKeySerializer(new GenericToStringSerializer(Object.class));
        setValueSerializer(new GenericJackson2JsonRedisSerializer());
        setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

    }
}