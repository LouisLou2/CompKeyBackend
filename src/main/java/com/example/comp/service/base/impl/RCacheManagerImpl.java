package com.example.comp.service.base.impl;

import com.example.comp.service.base.inter.RCacheManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
public class RCacheManagerImpl implements RCacheManager {
  @Resource
  private RedisTemplate<String, Object> redisTemplate;

  @Resource
  private RedisTemplate<String, Integer> redisTemplateInt;

  @Resource
  private RedisTemplate<String, String> redisTemplateVstr;

  private HashOperations<String, String, Object> opsForHash;
  private HashOperations<String, Integer, Object> opsForHashIntKey;
  private ZSetOperations<String, String> opsForZSet;
  private ZSetOperations<String, Integer> opsForZSetInt;
  private ListOperations<String, Object> opsForList;

  @PostConstruct
  void init() {
    opsForHash = redisTemplate.opsForHash();
    opsForHashIntKey = redisTemplate.opsForHash();
    opsForZSet = redisTemplateVstr.opsForZSet();
    opsForList = redisTemplate.opsForList();
    opsForZSetInt = redisTemplateInt.opsForZSet();
  }

  @Override
  public HashOperations<String, String, Object> getOpsForHash() {
    return opsForHash;
  }

  @Override
  public HashOperations<String, Integer, Object> getOpsForHashIntKey() {
    return opsForHashIntKey;
  }

  @Override
  public ZSetOperations<String, String> getOpsForZSet() {
    return opsForZSet;
  }

  @Override
  public ZSetOperations<String, Integer> getOpsForZSetInt() {
    return opsForZSetInt;
  }

  @Override
  public ListOperations<String, Object> getOpsForList() {
    return opsForList;
  }

  @Override
  public RedisTemplate<String, Object> getRedisObjTemplate() {
    return redisTemplate;
  }

  @Override
  public RedisTemplate<String, Object> debug_getRedisTemplate() {
    return redisTemplate;
  }
}