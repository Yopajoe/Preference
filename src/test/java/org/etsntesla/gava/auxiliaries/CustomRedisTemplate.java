package org.etsntesla.gava.auxiliaries;


import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

public class CustomRedisTemplate extends RedisTemplate<String,Object>  {

    TestProperties properties = new TestProperties();

    public CustomRedisTemplate() {
        setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        setKeySerializer(new GenericToStringSerializer(Object.class));
        setValueSerializer(new GenericJackson2JsonRedisSerializer());
        setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(properties.getProperty("app.redisUrl"));
        redisConfig.setPort(Integer.parseInt(properties.getProperty("app.redisPort")));
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig);
        lettuceConnectionFactory.afterPropertiesSet();
        setConnectionFactory(lettuceConnectionFactory);
        afterPropertiesSet();
    }

}
