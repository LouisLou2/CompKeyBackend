package com.example.comp.service.base.inter;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

public interface RCacheManager {
  HashOperations<String, String, Object> getOpsForHash();
  HashOperations<String, Integer, Object> getOpsForHashIntKey();
  ZSetOperations<String, String> getOpsForZSet();
  ZSetOperations<String, Integer> getOpsForZSetInt();
  ListOperations<String, Object> getOpsForList();

  RedisTemplate<String, Object> getRedisObjTemplate();
  RedisTemplate<String, Object> debug_getRedisTemplate();
}