package com.example.comp.usecase.impl;

import com.example.comp.dao.CompScoreDao;
import com.example.comp.entity.CompWord;
import com.example.comp.entity.RecoCompWord;
import com.example.comp.service.cache.RecoCompWordCache;
import com.example.comp.struct.NullablePair;
import com.example.comp.usecase.inter.CompScoreRetriever;
import com.example.comp.util.ListUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompScoreRetriverImpl implements CompScoreRetriever {

  @Resource
  private CompScoreDao compScoreDao;

  @Resource
  private RecoCompWordCache recoCompWordCache;

  @Override
  public List<CompWord> getCompWords(int wordId, int limit, int offset, boolean autoRenewCache) {
    List<RecoCompWord> lis = getRecoCompWords(wordId, limit, offset, autoRenewCache);
    return lis.stream().map(RecoCompWord::toCompWord).toList();
  }

  @Override
  public List<RecoCompWord> getRecoCompWords(int wordId, int limit, int offset, boolean autoRenewCache) {
    // 先去缓存中查找
    NullablePair<Boolean,List<RecoCompWord>> cacheResult = recoCompWordCache.getRecoCompWords(wordId, limit, offset);
    // 如果缓存中有数据，直接返回
    if (cacheResult.getFirst()) {
      return cacheResult.getSecond();
    }
    if (autoRenewCache) {
      // 如果需要自动更新缓存, 将该种子的所有竞争词插入
      List<RecoCompWord> allRecoCompWords = compScoreDao.getRecoCompWords(wordId, Integer.MAX_VALUE, 0);
      assert allRecoCompWords != null;
      if (allRecoCompWords.isEmpty()) {
        return allRecoCompWords;
      }
      recoCompWordCache.insertRecoCompWords(wordId, allRecoCompWords);
      return ListUtil.safeSubList(offset, limit, allRecoCompWords);
    }
    return compScoreDao.getRecoCompWords(wordId, limit, offset);
  }
}