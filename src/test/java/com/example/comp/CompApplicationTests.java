package com.example.comp;

import com.example.comp.entity.ScoreOpinion;
import com.example.comp.mapper.FeedBackMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class CompApplicationTests {

  @Resource
  private FeedBackMapper feedBackMapper;

  @Test
  void contextLoads() {
    List<ScoreOpinion> scores = feedBackMapper.getUnprocessedBySeedId(0, 10, 0, LocalDateTime.now().minusDays(1));
    System.out.println(scores);
  }

  @Test
  void testInsertFeedback() {
    feedBackMapper.insertFeedback("123.12.1.2", 0, 385, true, LocalDateTime.now());
  }

  @Test
  void testMarkAsProcessed() {
    feedBackMapper.markAsProcessed(1, 2);
  }

}
