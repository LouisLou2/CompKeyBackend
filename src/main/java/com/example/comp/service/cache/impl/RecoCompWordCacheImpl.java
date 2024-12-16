package com.example.comp.service.cache.impl;

import com.example.comp.entity.RecoCompWord;
import com.example.comp.service.base.inter.RCacheManager;
import com.example.comp.service.cache.RecoCompWordCache;
import com.example.comp.struct.NullablePair;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecoCompWordCacheImpl implements RecoCompWordCache {
  @Value("${container.redis.reco-words.list-prefix}")
  private String recoCompWordListPrefix;

  @Value("${container.redis.reco-words.lru-zset}")
  private String recoCompWordLRUZset;

  @Value("${container.redis.reco-words.max-capacity}")
  private int maxCapacity;

  @Resource
  private RCacheManager rCacheManager;

  private RedisTemplate<String, Object> redisTemplate;

  private ZSetOperations<String, Integer> zSetOps;

  final private String getRecoCompWordScript = """
  -- Check if wordId exists in the zset
  local zset_name = KEYS[1]
  local list_prefix = KEYS[2]
  local wordId = ARGV[1]
  local num = tonumber(ARGV[2])
  local offset = tonumber(ARGV[3])
  local timeStamp = tonumber(ARGV[4])
  
  -- Check if wordId exists in the zset
  local exists = redis.call("ZSCORE", zset_name, wordId)
  
  if not exists then
      -- If wordId does not exist in zset, return null
      return nil
  end
  -- If wordId exists in zset, return the list, and update the zset
  redis.call("ZADD", zset_name, timeStamp, wordId)
  
  -- Get elements from list:wordId with offset and num
  local result = redis.call("LRANGE", list_prefix .. ":" .. wordId, offset, offset + num - 1)
  
  return result
  """;

  final private String insertNewRecoCompWordScript = """
  -- 获取 zset 中的大小
  local zset_key = KEYS[1]
  local list_key = KEYS[2]
  local wordId = ARGV[1]
  local timeStamp = tonumber(ARGV[2])
  local max_num = tonumber(ARGV[3])
  
  -- 检查 zset 的大小
  local zset_size = redis.call("ZCARD", zset_key)
  
  if zset_size >= max_num then
      -- 如果 zset 已经满了，找到最大的时间戳和对应的 wordId
      local max_entry = redis.call("ZREVRANGE", zset_key, 0, 0, "WITHSCORES")
      local max_timeStamp = tonumber(max_entry[2])
      local max_wordId = max_entry[1]
  
      -- 移除最大时间戳的成员及对应的 list
      redis.call("ZREM", zset_key, max_wordId)
      redis.call("DEL", list_key .. ":" .. max_wordId)
  
      -- 返回移除的 wordId 和对应的时间戳
      return max_wordId
  end
  
  -- 如果 zset 没有满，插入新的数据
  redis.call("ZADD", zset_key, timeStamp, wordId)
  local listName = list_key .. ":" .. wordId
  -- 向 list 中插入新的数据（RecoComp 对象列表）
  for i = 4, #ARGV do
      -- ARGV 从第 4 项开始是 RecoComp 数据，这里假设每个 RecoComp 被序列化为 JSON 格式
      redis.call("RPUSH", listName, ARGV[i])
  end
  
  return wordId
  """;

  private final String forceUpdateListScript = """
  -- Lua 脚本：检查是否存在，若存在则删除，并插入新的 List
  local list_prefix = KEYS[1]
  local wordId = KEYS[2]
  local listName = list_prefix .. ":" .. wordId
  if redis.call('EXISTS', listName) == 1 then
      redis.call('DEL', listName)  -- 删除已有的 alist
      redis.call('RPUSH', listName, unpack(ARGV))
      return 2  -- 返回执行结果, 表示覆盖了已有的 List
  end
  redis.call('RPUSH', listName, unpack(ARGV))
  return 1  -- 返回执行结果，表示插入了新的 List
  """;

  private DefaultRedisScript<List> getRecoCompWordRedisScript;
  private DefaultRedisScript<Integer> insertRecoCompWordRedisScript;
  private DefaultRedisScript<Long> forceUpdateListRedisScript;

  @PostConstruct
  private void init(){
    redisTemplate = rCacheManager.getRedisObjTemplate();
    zSetOps = rCacheManager.getOpsForZSetInt();
    getRecoCompWordRedisScript = new DefaultRedisScript<>(getRecoCompWordScript, List.class);
    insertRecoCompWordRedisScript = new DefaultRedisScript<>(insertNewRecoCompWordScript, Integer.class);
    forceUpdateListRedisScript = new DefaultRedisScript<>(forceUpdateListScript, Long.class);
  }

  @Override
  public NullablePair<Boolean, List<RecoCompWord>> getRecoCompWords(int wordId, int limit, int offset) {
    List words = redisTemplate.execute(
      getRecoCompWordRedisScript,
      List.of(recoCompWordLRUZset, recoCompWordListPrefix),
      wordId,
      limit,
      offset,
      System.currentTimeMillis()
    );
    assert words != null;
    if (!words.isEmpty() && words.get(0) == null) return new NullablePair<>(false, null);
    List<RecoCompWord> recoCompWords = (List<RecoCompWord>) words;
    return new NullablePair<>(true, recoCompWords);
  }

  @Async("taskExecutor")
  @Override
  public void insertRecoCompWords(int wordId, List<RecoCompWord> recoCompWords) {
    long timeStamp = System.currentTimeMillis();
    List<Object> args = new ArrayList<>();
    args.add(wordId);
    args.add(timeStamp);
    args.add(maxCapacity);
    args.addAll(recoCompWords);
    Integer res = redisTemplate.execute(
      insertRecoCompWordRedisScript,
      List.of(recoCompWordLRUZset, recoCompWordListPrefix),
      args.toArray()
    );
    System.out.println(res);
  }

  @Override
  public boolean exists(int wordId) {
    return zSetOps.score(recoCompWordLRUZset, wordId) != null;
  }

  @Override
  public void updateList(int wordId, List<RecoCompWord> recoCompWords) {
    List<Object> args = new ArrayList<>(recoCompWords);
    Long res = redisTemplate.execute(
      forceUpdateListRedisScript,
      List.of(recoCompWordListPrefix, String.valueOf(wordId)),
      args.toArray()
    );
    System.out.println(res);
  }
}
