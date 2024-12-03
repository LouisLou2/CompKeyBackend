package com.example.comp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;


public class CompWord extends WithWord {

  // 自定义比较器：按 CompWord 的 score 降序排列
  public static class ScoreComparator implements Comparator<CompWord> {
    @Override
    public int compare(CompWord o1, CompWord o2) {
      return Double.compare(o1.getScore(), o2.getScore()); // 降序排列
    }
  }
  
  private int id;

  private String word;

  private double score;

  public CompWord() {
  }

  // setter and getter
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }
}
