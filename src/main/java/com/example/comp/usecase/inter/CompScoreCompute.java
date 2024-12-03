package com.example.comp.usecase.inter;

import com.example.comp.entity.CompWord;

import java.util.List;

public interface CompScoreCompute {
  List<List<CompWord>> compute(List<String> words, int reqNum);
}
