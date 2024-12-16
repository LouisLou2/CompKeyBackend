package com.example.comp.controller;

import com.example.comp.common.ResCodeEnum;
import com.example.comp.entity.CompWord;
import com.example.comp.entity.RecoCompWord;
import com.example.comp.entity.RecoScore;
import com.example.comp.entity.req.CompSeeds;
import com.example.comp.service.base.inter.WordMap;
import com.example.comp.struct.Pair;
import com.example.comp.struct.RespWrapper;
import com.example.comp.usecase.inter.RecoCompScoreCorrector;
import com.example.comp.usecase.inter.compute.CompScoreCompute;
import com.example.comp.usecase.inter.CompScoreRetriever;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/keywords/compkey")
public class CompWordController {

  @Resource
  private CompScoreRetriever compScoreRetriever;

  @Resource
  private WordMap wordMap;

  @Resource
  private RecoCompScoreCorrector recoCompScoreCorrector;

  @GetMapping("/reco_words")
  RespWrapper<?> getRecoCompWords(
    String word,
    @RequestParam @Min(1) @Max(10000) int limit,
    @RequestParam @Min(0) int offset
  ) {
    Integer wordId = wordMap.getIdByWord(word);
    List<RecoCompWord> result = compScoreRetriever.getRecoCompWords(wordId, limit, offset, true);
    List<RecoScore> recos = new ArrayList<>();
    for (RecoCompWord recoCompWord : result) {
      recos.add(new RecoScore(recoCompWord.getId(), recoCompWord.getRecoScore()));
    }
    recoCompScoreCorrector.correctRecoCompScore(wordId, recos);
    return RespWrapper.success(result);
  }

  @GetMapping("/reco_words_by_id")
  RespWrapper<?> getRecoCompWordsById(
    int wordId,
    @RequestParam @Min(1) @Max(10000) int limit,
    @RequestParam @Min(0) int offset
  ) {
    List<RecoCompWord> result = compScoreRetriever.getRecoCompWords(wordId, limit, offset, true);
    return RespWrapper.success(result);
  }

//  @GetMapping("/comp_words_force_compute")
//  RespWrapper<?> getCompWordsForceCompute(@RequestBody CompSeeds seeds) {
//    List<List<CompWord>> result = compScoreCompute.compute(seeds.getSeeds(), seeds.getReqNum());
//    return RespWrapper.success(result);
//  }

}
