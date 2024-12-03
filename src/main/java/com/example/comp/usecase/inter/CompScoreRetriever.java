package com.example.comp.usecase.inter;

import com.example.comp.entity.CompWord;
import com.example.comp.struct.Pair;

import java.util.List;

public interface CompScoreRetriever {
  Pair<Boolean,List<CompWord>> getCompWords(String word, int limit);
  List<CompWord> getCompWords(int wordId, int limit);
}
