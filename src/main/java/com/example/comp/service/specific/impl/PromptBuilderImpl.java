package com.example.comp.service.specific.impl;

import com.example.comp.entity.RecoCompWord;
import com.example.comp.service.specific.PromptBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilderImpl implements PromptBuilder{

  @Override
  public String buildPrompt(String word, List<RecoCompWord> candidates) {
    StringBuilder sb = new StringBuilder();
    sb.append("输入关键词是:")
      .append(word)
      .append("\n")
      .append("候选关键词有:");
    for (RecoCompWord candidate : candidates) {
      sb.append(candidate.getWord())
        .append(", ");
    }
    sb.append('\n');
    sb.append("请根据与输入关键词的相关性，将候选关键词排序，并赋予对应的分数，并返回这些关键词与用户输入关键词的相关性评分。返回的格式为:\n");
    sb.append("[{\"keyword\":\"keyword1\",\"score\":99.9},{\"keyword\":\"keyword2\",\"score\":10.0}]");
    return sb.toString();
  }
}