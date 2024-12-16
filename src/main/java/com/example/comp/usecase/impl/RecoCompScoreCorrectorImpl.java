package com.example.comp.usecase.impl;

import com.example.comp.dao.CompScoreDao;
import com.example.comp.dao.impl.CompScoreDaoImpl;
import com.example.comp.entity.RecoScore;
import com.example.comp.service.cache.RecoCompWordCache;
import com.example.comp.usecase.inter.RecoCompScoreCorrector;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecoCompScoreCorrectorImpl implements RecoCompScoreCorrector {

  @Resource
  private RecoCompWordCache recoCompWordCache;

  @Resource
  private CompScoreDao compScoreDao;

  @Override
  public void correctRecoCompScore(int wordId1, List<RecoScore> recos) {
    compScoreDao.correctRecoCompScore(wordId1, recos);
    if (recoCompWordCache.exists(wordId1)) {
      recoCompWordCache.updateList(
        wordId1,
        compScoreDao.getRecoCompWords(wordId1, Integer.MAX_VALUE, 0)
      );
    }
  }
}