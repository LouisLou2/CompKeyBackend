package com.example.comp.controller;

import com.example.comp.common.ResCodeEnum;
import com.example.comp.entity.CompWord;
import com.example.comp.entity.RecoCompWord;
import com.example.comp.entity.RecoScore;
import com.example.comp.entity.WordScore;
import com.example.comp.entity.req.CompSeeds;
import com.example.comp.entity.req.RelatedWord;
import com.example.comp.service.base.inter.WordMap;
import com.example.comp.service.specific.RecoScoreScaler;
import com.example.comp.service.specific.RequestInfoExtractor;
import com.example.comp.struct.Pair;
import com.example.comp.struct.RespWrapper;
import com.example.comp.usecase.inter.AIDeepSearcher;
import com.example.comp.usecase.inter.FeedbackHandler;
import com.example.comp.usecase.inter.RecoCompScoreCorrector;
import com.example.comp.usecase.inter.compute.CompScoreCompute;
import com.example.comp.usecase.inter.CompScoreRetriever;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/keywords/compkey")
public class CompWordController {

  @Resource
  private CompScoreRetriever compScoreRetriever;

  @Resource
  private WordMap wordMap;

  @Resource
  private RecoScoreScaler recoScoreScaler;

  @Resource
  private AIDeepSearcher aiDeepSearcher;

  @Resource
  private FeedbackHandler feedbackHandler;

  @Resource
  private RequestInfoExtractor reqInfoExtractor;

  @GetMapping("/reco_words")
  RespWrapper<?> getRecoCompWords(
    String word,
    @RequestParam @Min(1) @Max(10000) int limit,
    @RequestParam @Min(0) int offset
  ) {
    Integer wordId = wordMap.getIdByWord(word);
    if (wordId == null) {
      return RespWrapper.success(List.of());
    }
    List<RecoCompWord> result = compScoreRetriever.getRecoCompWords(wordId, limit, offset, true);
    recoScoreScaler.scale(result);
    return RespWrapper.success(
      Map.of(
        "wordId", wordId,
        "reco_list", result
      )
    );
  }

  @GetMapping("/reco_words_by_id")
  RespWrapper<?> getRecoCompWordsById(
    int wordId,
    @RequestParam @Min(1) @Max(10000) int limit,
    @RequestParam @Min(0) int offset
  ) {
    if (!wordMap.exists(wordId)) {
      return RespWrapper.success(List.of());
    }
    List<RecoCompWord> result = compScoreRetriever.getRecoCompWords(wordId, limit, offset, true);
    recoScoreScaler.scale(result);
    return RespWrapper.success(result);
  }

  @GetMapping("/ai_rank")
  Map<String,Object> getRecoWordsByAI(
      String word,
      @RequestParam @Min(1) @Max(10000) int limit,
      @RequestParam @Min(0) int offset
  ){
    Map<String,Object> res = new HashMap<>();
    List<WordScore> recos = new ArrayList<>();
    RelatedWord relatedWord = new RelatedWord(word, recos);
    res.put("status", "failed");
    res.put("data", relatedWord);

    Integer wordId = wordMap.getIdByWord(word);
    if (wordId == null) return res;
    List<RecoCompWord> result = compScoreRetriever.getRecoCompWords(wordId, limit, offset, true);
    try{
      recos = aiDeepSearcher.recommend(word, result);
    } catch (JsonProcessingException e) {
      return res;
    }
    relatedWord = new RelatedWord(word, recos);
    res.put("status", "success");
    res.put("data", relatedWord);
    return res;
  }

  @GetMapping("/ai_report")
  Map<String,Object> getWordReportByAI(
      String word,
      String compWord
  ){
    assert(word != null && compWord != null);
    Map<String,Object> res = new HashMap<>();
    String report = aiDeepSearcher.report(word, compWord);
    res.put("status", "success");
    res.put("data", Map.of("analysis", report));
    return res;
  }

  @GetMapping("/feedback")
  RespWrapper<?> feedback(
    int seedId,
    int compId,
    boolean opinion,
    HttpServletRequest request
  ){
    if (!wordMap.exists(seedId) || !wordMap.exists(compId)){
      return RespWrapper.error(ResCodeEnum.WordNotFound);
    }
    String ip = reqInfoExtractor.getIpv4(request);
    feedbackHandler.accept(ip,seedId, compId, opinion, LocalDateTime.now());
    return RespWrapper.success(null);
  }

  @GetMapping("/force_update")
  RespWrapper<?> forceUpdate(){
    feedbackHandler.autoUpdateRecoScores();
    return RespWrapper.success(null);
  }
}



//  @GetMapping("/ai_report")
//  Map<String,Object> getWordReportByAI(
//      String word,
//      @RequestParam @Min(1) @Max(10000) int limit,
//      @RequestParam @Min(0) int offset
//  ){
//    Map<String,Object> res = new HashMap<>();
//    String report ="";
//
//    Integer wordId = wordMap.getIdByWord(word);
//    if (wordId == null){
//      res.put("status", "failed");
//      res.put("data", Map.of("analysis", report));
//      return res;
//    }
//    List<RecoCompWord> result = compScoreRetriever.getRecoCompWords(wordId, limit, offset, true);
//    report = aiDeepSearcher.report(word, result);
//    res.put("status", "success");
//    res.put("data", Map.of("analysis", report));
//    return res;
//  }


//  @GetMapping("/ai_marketing_copy")
//  Map<String,Object> getWordMarketCopyByAI(
//      String word,
//      @RequestParam @Min(1) @Max(10000) int limit,
//      @RequestParam @Min(0) int offset
//  ){
//    Map<String,Object> res = new HashMap<>();
//    String report ="";
//
//    Integer wordId = wordMap.getIdByWord(word);
//    if (wordId == null){
//      res.put("status", "failed");
//      res.put("data", Map.of("analysis", report));
//      return res;
//    }
//    List<RecoCompWord> result = compScoreRetriever.getRecoCompWords(wordId, limit, offset, true);
//    report = aiDeepSearcher.report(word, result);
//    res.put("status", "success");
//    res.put("data", Map.of("analysis", report));
//    return res;
//  }

//  @GetMapping("/comp_words_force_compute")
//  RespWrapper<?> getCompWordsForceCompute(@RequestBody CompSeeds seeds) {
//    List<List<CompWord>> result = compScoreCompute.compute(seeds.getSeeds(), seeds.getReqNum());
//    return RespWrapper.success(result);
//  }


//    List<RecoScore> recos = new ArrayList<>();
//    for (RecoCompWord recoCompWord : result) {
//      recos.add(new RecoScore(recoCompWord.getId(), recoCompWord.getRecoScore()));
//    }
//    recoCompScoreCorrector.correctRecoCompScore(wordId, recos);
