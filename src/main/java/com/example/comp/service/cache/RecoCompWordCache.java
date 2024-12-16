package com.example.comp.service.cache;

import com.example.comp.entity.RecoCompWord;
import com.example.comp.struct.NullablePair;

import java.util.List;

public interface RecoCompWordCache {
  NullablePair<Boolean,List<RecoCompWord>> getRecoCompWords(int wordId, int limit, int offset);
  void insertRecoCompWords(int wordId, List<RecoCompWord> recoCompWords);
  boolean exists(int wordId);
  void updateList(int wordId, List<RecoCompWord> recoCompWords);
}
