package com.example.comp.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RecoScore {
  public int wordId;
  public double recoScore;
  
  public String toString() {
    return "RecoScore{" + "wordId=" + wordId + ", recoScore=" + recoScore + "}";
  }
}
