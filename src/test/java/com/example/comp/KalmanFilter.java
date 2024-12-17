package com.example.comp;

import java.util.Random;

public class KalmanFilter {

  private double x; // 初始状态 (关键词排名)
  private double P; // 初始估计误差协方差
  private final double A; // 状态转移矩阵 (假设关键词分数变化平稳)
  private final double H; // 观测矩阵 (假设用户反馈直接影响分数)
  private final double R; // 观测噪声方差 (用户反馈的噪声)
  private final double Q; // 过程噪声方差 (关键词分数的变化噪声)

  public KalmanFilter(double initialScore, double processVariance, double observationVariance) {
    this.x = initialScore; // 初始状态 (关键词排名)
    this.P = 1.0; // 初始估计误差协方差
    this.A = 1.0; // 状态转移矩阵
    this.H = 1.0; // 观测矩阵
    this.R = observationVariance; // 观测噪声方差
    this.Q = processVariance; // 过程噪声方差
  }

  public double update(double feedback) {
    // 1. 预测步骤 (状态预测)
    double xPred = this.A * this.x; // 预测下一状态（排名）
    double PPred = this.A * this.P * this.A + this.Q; // 预测误差协方差

    // 2. 更新步骤 (基于新的观测值更新状态)
    double K = PPred * this.H / (this.H * PPred * this.H + this.R); // 计算卡尔曼增益
    this.x = xPred + K * (feedback - this.H * xPred); // 更新状态
    this.P = (1 - K * this.H) * PPred; // 更新误差协方差

    return this.x; // 返回更新后的分数
  }

  public static void main(String[] args) {
    double initialScore = 0.12;
    double processVariance = 0.00005;  // 过程噪声方差（关键词变化的自然噪声）
    double observationVariance = 0.0005;  // 观测噪声方差（用户反馈的噪声）

    // 创建卡尔曼滤波器实例
    KalmanFilter kf = new KalmanFilter(initialScore, processVariance, observationVariance);

    Random random = new Random();
    int numFeedbacks = 2000;  // 模拟的反馈次数

    // 模拟用户反馈，每次反馈会调整分数
    double updatedScore = initialScore;
    for (int i = 0; i < numFeedbacks; i++) {
      // 生成正态分布的反馈，标准差为0.002
      double feedback = random.nextGaussian() * 0.002;
      feedback += updatedScore;  // 假设反馈加到当前分数上
      updatedScore = kf.update(feedback);  // 更新分数

      // 输出每次反馈后的关键词排名
      System.out.printf("第%d次反馈: %.4f -> 更新后的分数: %.4f%n", i + 1, feedback, updatedScore);
    }
  }
}
