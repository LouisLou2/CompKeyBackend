package com.example.comp.dao.impl;

import com.example.comp.dao.CompScoreDao;
import com.example.comp.entity.CompWord;
import com.example.comp.entity.RecoCompWord;
import com.example.comp.entity.RecoScore;
import com.example.comp.service.base.inter.Neo4jConnector;
import jakarta.annotation.Resource;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompScoreDaoImpl implements CompScoreDao {

  @Resource
  private Neo4jConnector neo4jConnector;

  final String getComputedCompWordsScript = """
      MATCH (w:SWORD)-[r:SCORE]->(n:SWORD)
      WHERE w.wordId = $wordId
      RETURN n.wordId AS targetId, r.score AS compScore, n.word AS word
      ORDER BY compScore DESC
      SKIP $offset
      LIMIT $limit
  """;

  final String getComputedRecoCompWordsScript = """
      MATCH (w:SWORD)-[r:RECO_SCORE]->(n:SWORD)
      WHERE w.wordId = $wordId
      WITH n.wordId AS targetId, r.score AS recoScore, n.word AS word
      MATCH (w:SWORD)-[r2:SCORE]->(n2:SWORD)
      WHERE w.wordId = $wordId AND n2.wordId = targetId
      RETURN targetId, COLLECT(r2.score)[0] AS compScore, recoScore, word
      ORDER BY recoScore DESC
      SKIP $offset
      LIMIT $limit
  """;

  final String correctRecoCompScoreScript = """
  MATCH (w1:SWORD {wordId: $wordId1})-[r:RECO_SCORE]->(w2:SWORD {wordId: $wordId2})
  SET r.score = $score
  """;

  final String correctRecoCompScoresScript = """
  UNWIND $recos AS reco
  MATCH (w1:SWORD {wordId: $wordId1})-[r:RECO_SCORE]->(w2:SWORD {wordId: reco.wordId})
  SET r.score = reco.score
  """;

  @Override
  public List<CompWord> getCompWords(int wordId, int limit, int offset) {
    Result result = neo4jConnector.getSession().run(
        getComputedCompWordsScript,
        Map.of("wordId", wordId, "limit", limit, "offset", offset)
    );
    List<CompWord> compWords = new ArrayList<>();
    while (result.hasNext()) {
      Record record = result.next();
      CompWord word = new CompWord();
      word.setId(record.get("targetId").asInt());
      word.setWord(record.get("word").asString());
      word.setCompScore(record.get("compScore").asDouble());
      compWords.add(word);
    }
    return compWords;
  }

  @Override
  public List<RecoCompWord> getRecoCompWords(int wordId, int limit, int offset) {
    Result result = neo4jConnector.getSession().run(
        getComputedRecoCompWordsScript,
        Map.of("wordId", wordId, "limit", limit, "offset", offset)
    );
    List<RecoCompWord> compWords = new ArrayList<>();
    while (result.hasNext()) {
      Record record = result.next();
      RecoCompWord word = new RecoCompWord();
      word.setId(record.get("targetId").asInt());
      word.setWord(record.get("word").asString());
      word.setCompScore(record.get("compScore").asDouble());
      word.setRecoScore(record.get("recoScore").asDouble());
      compWords.add(word);
    }
    return compWords;
  }

  @Override
  public void correctRecoCompScore(int wordId1, int wordId2, int score) {
    neo4jConnector.getSession().run(
        correctRecoCompScoreScript,
        Map.of("wordId1", wordId1, "wordId2", wordId2, "score", score)
    );
  }

  @Override
  public void correctRecoCompScore(int wordId1, List<RecoScore> recos) {
    // 将 recos 列表中的每个 RecoScore 转换为 Map 格式
    List<Map<String, Object>> recoMaps = recos.stream().map(reco -> {
      Map<String, Object> recoMap = new HashMap<>();
      recoMap.put("wordId", reco.wordId);
      recoMap.put("score", reco.recoScore);
      return recoMap;
    }).toList();
    // 将转换后的 recos 作为参数传递给 Cypher 查询
    neo4jConnector.getSession().run(
        correctRecoCompScoresScript,
        Map.of("wordId1", wordId1, "recos", recoMaps)
    );
  }
}
