package com.example.comp.service.specific.impl;

import com.example.comp.entity.RecoCompWord;
import com.example.comp.service.specific.RecoScoreScaler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecoScoreScalerImpl implements RecoScoreScaler {

  @Override
  public void scale(List<RecoCompWord> lis) {
    // max-min scale
    double max = 0;
    double min = Double.MAX_VALUE;
    for (RecoCompWord recoCompWord : lis) {
      // 比较
      if (recoCompWord.getRecoScore() > max) {
        max = recoCompWord.getRecoScore();
      }
      if (recoCompWord.getRecoScore() < min) {
        min = recoCompWord.getRecoScore();
      }
    }
    double range = max - min;
    if (range == 0) {
      //那么所有的都是一样的，设置为100
      for (RecoCompWord recoCompWord : lis) {
        recoCompWord.setRecoScoreScaled(100);
      }
    }
    for (RecoCompWord recoCompWord : lis) {
      recoCompWord.setRecoScoreScaled(((recoCompWord.getRecoScore() - min) / range)*90+10);
    }
  }
}