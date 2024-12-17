package com.example.comp.usecase.inter;

import java.time.LocalDateTime;

public interface FeedbackHandler {
  void accept(String ipv4, int seedId, int compId, boolean opinion, LocalDateTime time);
  void autoUpdateRecoScores();
}
