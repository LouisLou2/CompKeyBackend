package com.example.comp.controller;

import com.example.comp.common.ResCodeEnum;
import com.example.comp.entity.CompWord;
import com.example.comp.entity.req.CompSeeds;
import com.example.comp.struct.Pair;
import com.example.comp.struct.RespWrapper;
import com.example.comp.usecase.inter.CompScoreCompute;
import com.example.comp.usecase.inter.CompScoreRetriever;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/keywords/compkey")
public class CompWordController {

  @Resource
  private CompScoreRetriever compScoreRetriever;

  @Resource
  private CompScoreCompute compScoreCompute;

  @GetMapping("/comp_words")
  RespWrapper<?> getCompWords(String word, int limit) {
    Pair<Boolean, List<CompWord>> result = compScoreRetriever.getCompWords(word, limit);
    if (!result.getFirst()) {
      return RespWrapper.error(ResCodeEnum.WordNotFound);
    }
    return RespWrapper.success(result.getSecond());
  }

  @GetMapping("/comp_words_force_compute")
  RespWrapper<?> getCompWordsForceCompute(@RequestBody CompSeeds seeds) {
    List<List<CompWord>> result = compScoreCompute.compute(seeds.getSeeds(), seeds.getReqNum());
    return RespWrapper.success(result);
  }
}
