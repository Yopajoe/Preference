package org.etsntesla.gava.configurations;


import org.etsntesla.gava.utils.AppProperties;
import org.etsntesla.gava.utils.CustomRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;


@Configuration
public class RedisConfig extends AbstractHttpSessionApplicationInitializer {


    @Autowired
    AppProperties appProperties;

    @Bean
    @SpringSessionRedisConnectionFactory
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(appProperties.getRedisUrl());
        redisConfig.setPort(appProperties.getRedisPort());

        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public CustomRedisTemplate customRedisTemplate(){
        CustomRedisTemplate customRedisTemplate = new CustomRedisTemplate();
        customRedisTemplate.setConnectionFactory(connectionFactory());
        return customRedisTemplate;
    }


}