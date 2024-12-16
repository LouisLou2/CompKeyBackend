package com.example.comp.entity;

import lombok.*;

import java.util.Comparator;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompWord extends WithWord {
  // 自定义比较器：按 CompWord 的 score 降序排列
  public static class ScoreComparator implements Comparator<CompWord> {
    @Override
    public int compare(CompWord o1, CompWord o2) {
      return Double.compare(o1.getCompScore(), o2.getCompScore()); // 降序排列
    }
  }

  private int id;

  private String word;

  private double compScore;
}
