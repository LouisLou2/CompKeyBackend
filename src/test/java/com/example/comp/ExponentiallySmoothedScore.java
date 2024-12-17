package com.example.comp;

public class ExponentiallySmoothedScore {

  private double smoothedScore; // 平滑后的分数
  private final double alpha;   // 平滑系数

  // 构造函数
  public ExponentiallySmoothedScore(double initialScore, double alpha) {
    this.smoothedScore = initialScore;
    this.alpha = alpha;
  }

  // 更新平滑分数
  public double update(double feedback) {
    smoothedScore = alpha * feedback + (1 - alpha) * smoothedScore;
    return smoothedScore;
  }

  // 获取当前平滑分数
  public double getSmoothedScore() {
    return smoothedScore;
  }

  // 主方法示例，模拟反馈并打印平滑分数
  public static void main(String[] args) {
    double initialScore = 0.12;  // 初始分数
    double alpha = 0.01;         // 平滑系数

    // 创建指数平滑实例
    ExponentiallySmoothedScore smoother = new ExponentiallySmoothedScore(initialScore, alpha);

    // 模拟用户反馈并更新平滑分数
    for (int i = 0; i < 10000; i++) {
      double feedback = smoother.getSmoothedScore() - 0.003; // 用户反馈（例如：负反馈）
      double updatedScore = smoother.update(feedback);
      System.out.printf("第%d次反馈: %.4f -> 平滑分数: %.4f%n", i + 1, feedback, updatedScore);
    }
  }
}
