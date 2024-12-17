package com.example.comp;

import com.example.comp.usecase.inter.FeedbackHandler;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AutoUpdateScoreTest {
  @Resource
  private FeedbackHandler feedbackHandler;

  @Test
  void testUpdate() {
    feedbackHandler.autoUpdateRecoScores();
  }
}
