package com.example.comp.usecase.impl;

import com.example.comp.entity.RecoCompWord;
import com.example.comp.entity.RecoScore;
import com.example.comp.entity.ScoreOpinion;
import com.example.comp.mapper.FeedBackMapper;
import com.example.comp.service.compute.ScoreOptimizer;
import com.example.comp.service.compute.impl.KalmanFilterOptimizer;
import com.example.comp.usecase.inter.CompScoreRetriever;
import com.example.comp.usecase.inter.FeedbackHandler;
import com.example.comp.usecase.inter.RecoCompScoreCorrector;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackHandlerImpl implements FeedbackHandler {

  @Resource
  private FeedBackMapper feedBackMapper;

  @Resource
  private RecoCompScoreCorrector recoCorrector;

  @Resource
  private CompScoreRetriever compScoreRetriever;


  @Override
  public void accept(String ipv4, int seedId, int compId, boolean opinion, LocalDateTime time) {
    feedBackMapper.insertFeedback(ipv4, seedId, compId, opinion, time);
  }

  // 每周二凌晨 2:00 执行
  @Scheduled(cron = "0 0 2 ? * 2")
  @Async("taskExecutor")
  @Override
  public void autoUpdateRecoScores() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime minTime = now.minusMonths(1); // 如果一个月前有未处理的反馈，不再将其纳入计算
    int maxRecordsToProcess = 10000;
    List<Integer> seedIds = feedBackMapper.getUnprocessedSeedIds(minTime, now);

    for (int seedId : seedIds) {
      Map<Integer,List<Boolean>> seedOpinions = new HashMap<>();
      // 为种子关键词Id为seedId的种子关键词的推荐词更新分数
      List<ScoreOpinion> compOps = feedBackMapper.getUnprocessedBySeedId(seedId, maxRecordsToProcess, 0, minTime);
      for (ScoreOpinion compOp : compOps) {
        seedOpinions.putIfAbsent(compOp.compId, new ArrayList<>());
        seedOpinions.get(compOp.compId).add(compOp.opinion);
      }
      // 获取原始分数
      List<RecoCompWord> originalScores = compScoreRetriever.getRecoCompWords(seedId, Integer.MAX_VALUE, 0, false);
      List<RecoScore> updatedScores = updateForSeed(originalScores, seedOpinions);

      System.out.println("@@@@@@@@@@@For SeedId: " + seedId);
      System.out.println("将执行的修改");
      for (RecoScore recoScore : updatedScores) {
        System.out.println(recoScore);
      }

      recoCorrector.correctRecoCompScore(seedId, updatedScores);
      // 标记为已处理
      if (!compOps.isEmpty()) {
        feedBackMapper.markAsProcessed(compOps.get(0).id, compOps.get(compOps.size() - 1).id);
      }
    }
  }

  private List<RecoScore> updateForSeed(List<RecoCompWord> original, Map<Integer, List<Boolean>> seedOpinions) {
    List<RecoScore> updatedScores = new ArrayList<>();
    int compId;
    double originalScore;
    double updatedScore;
    for (RecoCompWord reco : original) {
      compId = reco.getId();
      originalScore = reco.getRecoScore();
      List<Boolean> opinions = seedOpinions.get(compId);
      if (opinions == null) continue; // 没有反馈
      updatedScore = updateForEdgeUsingKalman(originalScore, opinions);
      updatedScores.add(new RecoScore(compId, updatedScore));
    }
    return updatedScores;
  }

  private double updateForEdgeUsingKalman(double originalScore, List<Boolean> opinions) {
    double processVariance = 0.00005;  // 过程噪声方差（关键词变化的自然噪声）
    double observationVariance = 0.0005;  // 观测噪声方差（用户反馈的噪声）
    ScoreOptimizer optimizer = new KalmanFilterOptimizer(originalScore, processVariance, observationVariance);
    double score = originalScore;
    for (boolean opinion : opinions) {
      score = optimizer.optimize(opinion ? score + 0.0001 : score-0.0001);
    }
    return score;
  }
}