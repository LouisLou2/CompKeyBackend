package com.example.comp.usecase.impl;

import com.example.comp.entity.CompWord;
import com.example.comp.service.inter.Neo4jConnector;
import com.example.comp.service.inter.WordMap;
import com.example.comp.struct.Pair;
import com.example.comp.usecase.inter.CompScoreRetriever;
import jakarta.annotation.Resource;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CompScoreRetriverImpl implements CompScoreRetriever {
  @Resource
  private WordMap wordMap;
  @Resource
  private Neo4jConnector neo4jConnector;

  final String getComputedCompWordsScript = """
      MATCH (w:SWORD)-[r:SCORE]->(n:SWORD)
      WHERE w.wordId = $wordId
      RETURN n.wordId AS targetId, r.score AS score
      ORDER BY r.score DESC
      LIMIT $limit
  """;

  @Override
  public Pair<Boolean,List<CompWord>> getCompWords(String word,int limit) {
    Integer id = wordMap.getIdByWord(word);
    if(id == null) return new Pair<>(false, null);
    List<CompWord> compWords = getCompWords(id, limit);
    wordMap.setWordFor(compWords);
    return new Pair<>(true, compWords);
  }

  @Override
  public List<CompWord> getCompWords(int wordId, int limit) {
    Result result = neo4jConnector.getSession().run(
        getComputedCompWordsScript,
        Map.of("wordId", wordId, "limit", limit)
    );
    List<CompWord> compWords = new ArrayList<>();
    while (result.hasNext()) {
      Record record = result.next();
      CompWord word = new CompWord();
      word.setId(record.get("targetId").asInt());
      word.setScore(record.get("score").asDouble());
      compWords.add(word);
    }
    return compWords;
  }
}