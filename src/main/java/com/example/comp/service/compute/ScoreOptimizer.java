package com.example.comp.service.compute;

public interface ScoreOptimizer {
  double optimize(double recommendScore);
  double nowScore();
}