package com.example.comp.mapper;

import com.example.comp.entity.ScoreOpinion;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FeedBackMapper {
  // List<ScoreOpinion> getUnprocessedOpinions(int seedId, LocalDateTime since, LocalDateTime to, int limit, int offset);
  List<ScoreOpinion> getUnprocessedOpinionsByTime(int limit, int offset);
  void insertFeedback(String ipv4, int seedId, int compId, boolean opinion, LocalDateTime time);
  void markAsProcessed(int idFrom, int idTo);
  List<Integer> getUnprocessedSeedIds(LocalDateTime from, LocalDateTime to);
  List<ScoreOpinion> getUnprocessedBySeedId(int seedId, int limit, int offset, LocalDateTime minTime);
}
