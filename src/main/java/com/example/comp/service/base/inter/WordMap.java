package com.example.comp.service.base.inter;

import com.example.comp.entity.WithWord;

import java.util.List;

public interface WordMap {
  Integer getIdByWord(String word);
  void setWordFor(List<? extends WithWord>words);
  Integer getOccurance(int wordId);
}
