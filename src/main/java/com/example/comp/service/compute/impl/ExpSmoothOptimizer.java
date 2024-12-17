package com.example.comp.service.compute.impl;

import com.example.comp.service.compute.ScoreOptimizer;

public class ExpSmoothOptimizer implements ScoreOptimizer {

  private double smoothedScore; // 平滑后的分数
  private final double alpha;   // 平滑系数

  // 构造函数
  public ExpSmoothOptimizer(double initialScore, double alpha) {
    this.smoothedScore = initialScore;
    this.alpha = alpha;
  }

  @Override
  public double optimize(double recommendScore) {
    smoothedScore = alpha * recommendScore + (1 - alpha) * smoothedScore;
    return smoothedScore;
  }

  @Override
  public double nowScore() {
    return smoothedScore;
  }
}