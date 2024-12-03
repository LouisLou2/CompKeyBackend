package com.example.comp.usecase.impl;

import com.example.comp.entity.CompWord;
import com.example.comp.service.inter.Neo4jConnector;
import com.example.comp.service.inter.WordMap;
import com.example.comp.usecase.inter.CompScoreCompute;
import jakarta.annotation.Resource;
import org.neo4j.driver.Result;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompScoreComputeImpl implements CompScoreCompute {

  @Resource
  private WordMap wordMap;
  @Resource
  private Neo4jConnector neo4jConnector;

  private final String getAllRelatedEdgeScript = """
  MATCH (w:SWORD)-[r:CO_OCCUR]-(n:SWORD)
  WHERE w.wordId IN $wordIds
  WITH w, n, r.num AS num
  RETURN
         CASE WHEN w.wordId < n.wordId THEN w.wordId ELSE n.wordId END AS id1,
         CASE WHEN w.wordId < n.wordId THEN n.wordId ELSE w.wordId END AS id2,
         num
  UNION
  
  MATCH (w:SWORD)-[r:CO_OCCUR]-(n:SWORD)-[r2:CO_OCCUR]-(n2:SWORD)
  WHERE w.wordId IN $wordIds AND n2 <> w  // 排除直接邻居
  WITH n.wordId AS id1, n2.wordId AS id2, r2.num AS num
  RETURN DISTINCT
         CASE WHEN id1 < id2 THEN id1 ELSE id2 END AS id1,
         CASE WHEN id1 < id2 THEN id2 ELSE id1 END AS id2,
         num
  """;

  public static List<CompWord> partialSort(List<CompWord> list, int k) {

    CompWord.ScoreComparator comparator = new CompWord.ScoreComparator();
    // 使用自定义比较器创建优先队列（最大堆）
    PriorityQueue<CompWord> pq = new PriorityQueue<>(comparator);
    int makeHeapUsed = Math.min(k, list.size());
    pq.addAll(list.subList(0, makeHeapUsed));
    // 大堆制作完毕
    for (int i = makeHeapUsed; i < list.size(); i++) {
      CompWord obj = list.get(i);
      assert pq.peek() != null;
      if (comparator.compare(obj, pq.peek()) > 0) {
        pq.poll();
        pq.add(obj);
      }
    }
    // 将排序后的前 k 个元素提取出来并构成新的子列表
    List<CompWord> result = new ArrayList<>();
    while (!pq.isEmpty()) {
      result.add(pq.poll());
    }
    //reverse
    Collections.reverse(result);
    return result;
  }

  private List<CompWord> computeOne(HashMap<Integer,HashMap<Integer,Integer>> map, Integer s, int reqNum){
    assert s != null;
    final HashMap<Integer,Double> kscores = new HashMap<>();
    final HashMap<Integer, Integer> directEdges = map.get(s); // first is id2, second is num
    if (directEdges == null) {
      return new ArrayList<>();
    }
    for (Map.Entry<Integer, Integer> entry : directEdges.entrySet()) {
      int a= entry.getKey();
      int sa = entry.getValue();
      double c = (double)sa / wordMap.getOccurance(s) / (wordMap.getOccurance(a) - sa);
      final HashMap<Integer, Integer> indirectEdges = map.get(a);
      for (Map.Entry<Integer, Integer> entry2 : indirectEdges.entrySet()) {
        int k = entry2.getKey();
        int ka = entry2.getValue();
        if (k == s) {
          continue;
        }
        kscores.put(k, kscores.getOrDefault(k, 0.0) + c * ka);
      }
    }
    List<CompWord> result = new ArrayList<>();
    // reserving space for the result
    for (Map.Entry<Integer, Double> entry : kscores.entrySet()) {
      CompWord compWord = new CompWord();
      compWord.setId(entry.getKey());
      compWord.setScore(entry.getValue());
      result.add(compWord);
    }
    // sort
    return partialSort(result, reqNum);
  }

  private List<List<CompWord>> compute(HashMap<Integer,HashMap<Integer,Integer>> map, List<Integer> needIds, int reqNum){
    List<List<CompWord>> result = new ArrayList<>();
    for (Integer id : needIds) {
      result.add(computeOne(map, id, reqNum));
    }
    return result;
  }

  @Override
  public List<List<CompWord>> compute(List<String> words, int reqNum) {
    List<Integer> wordIds = new ArrayList<>();
    for (String word : words) {
      Integer id = wordMap.getIdByWord(word);
      if (id != null) {
        wordIds.add(id);
      }
    }
    Result result = neo4jConnector.getSession().run(
        getAllRelatedEdgeScript,
        Map.of("wordIds", wordIds)
    );
    HashMap<Integer,HashMap<Integer,Integer>> edgeMap = new HashMap<>();
    while (result.hasNext()) {
      var record = result.next();
      int id1 = record.get("id1").asInt();
      int id2 = record.get("id2").asInt();
      int num = record.get("num").asInt();
      edgeMap.putIfAbsent(id1, new HashMap<>());
      edgeMap.get(id1).put(id2, num);
      edgeMap.putIfAbsent(id2, new HashMap<>());
      edgeMap.get(id2).put(id1, num);
    }
    List<List<CompWord>> resultLists = compute(edgeMap, wordIds, reqNum);
    resultLists.forEach(list -> wordMap.setWordFor(list));
    return resultLists;
  }
}
