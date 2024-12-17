package com.example.comp.service.specific;

import com.example.comp.entity.RecoCompWord;

import java.util.List;

public interface PromptBuilder {
  String buildPrompt(String word, List<RecoCompWord> candidates);
}
