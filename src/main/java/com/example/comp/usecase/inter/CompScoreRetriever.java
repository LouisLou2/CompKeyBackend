package com.example.comp.usecase.inter;

import com.example.comp.entity.CompWord;
import com.example.comp.entity.RecoCompWord;
import com.example.comp.struct.Pair;

import java.util.List;

public interface CompScoreRetriever {
  List<CompWord> getCompWords(int wordId, int limit, int offset, boolean autoRenewCache);
  List<RecoCompWord> getRecoCompWords(int wordId, int limit, int offset, boolean autoRenewCache);
}
