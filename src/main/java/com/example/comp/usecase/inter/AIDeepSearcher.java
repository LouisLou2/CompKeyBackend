package com.example.comp.usecase.inter;

import com.example.comp.entity.RecoCompWord;
import com.example.comp.entity.WordScore;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface AIDeepSearcher {
  List<WordScore> recommend(String queryWord, List<RecoCompWord> candidates) throws JsonProcessingException;
  String report(String queryWord, List<RecoCompWord> candidates);
  String report(String queryWord, String compWord);
}
