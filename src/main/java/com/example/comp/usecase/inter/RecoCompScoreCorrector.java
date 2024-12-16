package com.example.comp.usecase.inter;

import com.example.comp.entity.RecoScore;

import java.util.List;

public interface RecoCompScoreCorrector {
  // void correctRecoCompScore(int wordId1, int wordId2, int score);

  // 保证recos是wordId1的所有推荐词的完整信息,不能是部分信息
  void correctRecoCompScore(int wordId1, List<RecoScore> recos);
}
