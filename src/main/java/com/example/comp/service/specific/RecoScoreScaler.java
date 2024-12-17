package com.example.comp.service.specific;

import com.example.comp.entity.RecoCompWord;

import java.util.List;

public interface RecoScoreScaler {
  void scale(List<RecoCompWord> lis);
}
