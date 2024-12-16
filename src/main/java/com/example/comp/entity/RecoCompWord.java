package com.example.comp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecoCompWord {

  // 自定义比较器：按 reco_score 降序排列
  public static class ScoreComparator implements Comparator<RecoCompWord> {
    @Override
    public int compare(RecoCompWord o1, RecoCompWord o2) {
      return Double.compare(o1.recoScore, o2.recoScore); // 降序排列
    }
  }

  private int id;

  private String word;

  private double compScore;

  private double recoScore;

  public CompWord toCompWord() {
    return new CompWord(id, word, compScore);
  }
}
