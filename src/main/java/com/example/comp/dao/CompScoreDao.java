package com.example.comp.dao;

import com.example.comp.entity.CompWord;
import com.example.comp.entity.RecoCompWord;
import com.example.comp.entity.RecoScore;
import com.example.comp.struct.Pair;

import java.util.List;

public interface CompScoreDao {
  List<CompWord> getCompWords(int wordId, int limit, int offset);
  List<RecoCompWord> getRecoCompWords(int wordId, int limit, int offset);
  void correctRecoCompScore(int wordId1, int wordId2, int score);
  void correctRecoCompScore(int wordId1, List<RecoScore> recos);
}
