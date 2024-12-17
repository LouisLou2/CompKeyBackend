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
    String prompt = promptBuilder.buildPrompt(queryWord, candidates);
    String modelOutput;
    try{
      modelOutput = aiCompleter.complete(prompt, queryWord);
      String primaryChoice = modelOutputParser.modelOutputPrimaryChoice(modelOutput);
      return parseWordScores(primaryChoice); // 可以是null
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  List<WordScore> parseWordScores(String primaryChoice) throws JsonProcessingException {
    return objectMapper.readValue(
        primaryChoice,
        objectMapper.getTypeFactory().constructCollectionType(List.class, WordScore.class)
    );
  }
}