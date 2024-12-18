package com.example.comp.usecase.impl;

import com.example.comp.entity.RecoCompWord;
import com.example.comp.entity.WordScore;
import com.example.comp.service.ai.AICompleter;
import com.example.comp.service.ai.ModelOutputParser;
import com.example.comp.service.specific.PromptBuilder;
import com.example.comp.usecase.inter.AIDeepSearcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIDeepSearcherImpl implements AIDeepSearcher {

  @Resource(name="OpenAICompleterImpl")
  private AICompleter aiCompleter;

  @Resource
  private PromptBuilder promptBuilder;

  @Resource(name="OpenAIOutputParserImpl")
  private ModelOutputParser modelOutputParser;

  final private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public List<WordScore> recommend(String queryWord, List<RecoCompWord> candidates){
    String prompt = promptBuilder.buildRecoPrompt(queryWord, candidates);
    String modelOutput;
    try{
      modelOutput = aiCompleter.complete(prompt, "");
      String primaryChoice = modelOutputParser.modelOutputPrimaryChoice(modelOutput);
      return parseWordScores(primaryChoice); // 可以是null
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String report(String queryWord, List<RecoCompWord> candidates) {
    String prompt = promptBuilder.buildReportPrompt(queryWord, candidates);
    try {
      String modelOutput = aiCompleter.complete(prompt, "");
      return modelOutputParser.modelOutputPrimaryChoice(modelOutput);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String report(String queryWord, String compWord) {
    String prompt = promptBuilder.buildReportPrompt(queryWord, compWord);
    try {
      String modelOutput = aiCompleter.complete(prompt, "");
      return modelOutputParser.modelOutputPrimaryChoice(modelOutput);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private List<WordScore> parseWordScores(String primaryChoice) throws JsonProcessingException {
    return objectMapper.readValue(
        primaryChoice,
        objectMapper.getTypeFactory().constructCollectionType(List.class, WordScore.class)
    );
  }
}