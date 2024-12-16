package com.example.comp.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // 配置 ObjectMapper
    ObjectMapper mapper = new ObjectMapper();
    mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY
    );
    mapper.registerModule(new JavaTimeModule());

    // 使用带 ObjectMapper 的构造器
    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(mapper, Object.class);
    StringRedisSerializer strSerializer = new StringRedisSerializer();
    // 设置 Redis 键和值的序列化方式
    template.setKeySerializer(strSerializer);
    template.setHashKeySerializer(strSerializer);
    template.setValueSerializer(serializer);
    template.setHashValueSerializer(serializer);

    return template;
  }

  @Bean
  public RedisTemplate<String, Integer> redisTemplateStrInt(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Integer> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // 配置 ObjectMapper
    ObjectMapper mapper = new ObjectMapper();
    mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY
    );
    mapper.registerModule(new JavaTimeModule());

    // 使用 Jackson2JsonRedisSerializer 来序列化 Integer 类型的值
    Jackson2JsonRedisSerializer<Integer> integerJsonSerializer = new Jackson2JsonRedisSerializer<>(Integer.class);

    // 使用 StringRedisSerializer 来序列化 String 类型的键
    StringRedisSerializer strSerializer = new StringRedisSerializer();

    // 设置 Redis 键和值的序列化方式
    template.setKeySerializer(strSerializer);          // 键使用 StringRedisSerializer
    template.setHashKeySerializer(strSerializer);     // Hash 键使用 StringRedisSerializer
    template.setValueSerializer(integerJsonSerializer); // 值使用 Jackson2JsonRedisSerializer<Integer>
    template.setHashValueSerializer(integerJsonSerializer); // Hash 值使用 Jackson2JsonRedisSerializer<Integer>

    return template;
  }
}