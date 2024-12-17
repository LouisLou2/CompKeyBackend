package com.example.comp.service.specific;

import com.example.comp.entity.RecoCompWord;

import java.util.List;

public interface PromptBuilder {
  String buildRecoPrompt(String word, List<RecoCompWord> candidates);
  String buildReportPrompt(String word, List<RecoCompWord> candidates);
}
