package com.example.comp.service.specific.impl;

import com.example.comp.entity.RecoCompWord;
import com.example.comp.service.specific.PromptBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilderImpl implements PromptBuilder{

  @Override
  public String buildRecoPrompt(String word, List<RecoCompWord> candidates) {
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

  @Override
  public String buildReportPrompt(String word, List<RecoCompWord> candidates) {
    StringBuilder sb = new StringBuilder();
    // 使用 StringBuilder 构造 prompt
    StringBuilder promptBuilder = new StringBuilder();
    promptBuilder.append("请撰写一篇详细分析报告，解释为什么基于用户提供的种子关键词:")
        .append(word)
        .append(",系统会推荐以下竞争关键词:\n");
        //.append("(竞争关键词指的是相关性高的关键词)\n");
        for (RecoCompWord candidate : candidates) {
          promptBuilder.append(candidate.getWord()).append(", ");
        }
        promptBuilder.append("\n")
        .append("分析报告内容要求：\n")
        .append("1. 对种子关键词进行定义和背景介绍，说明其核心意义及重要性；\n")
        .append("2. 分析竞争关键词与种子关键词之间的关联性，包括相似性或互补性；\n")
        .append("3. 针对竞争关键词，讨论其流行趋势、典型应用场景和潜在竞争优势；\n")
        .append("4. 综合分析，总结竞争关键词的价值，并提供相关优化或扩展建议。\n\n")
        .append("请用专业、流畅且逻辑清晰的语言完成以上内容。");
    return promptBuilder.toString();
  }

  @Override
  public String buildReportPrompt(String word, String compWord) {
    StringBuilder sb = new StringBuilder();
    sb.append("请撰写一篇详细分析报告，解释为什么基于用户提供的种子关键词:")
      .append(word)
      .append(",系统会推荐竞争关键词:")
      .append(compWord)
      .append("\n")
      .append("分析报告内容要求：\n")
      .append("1. 对种子关键词进行定义和背景介绍，说明其核心意义及重要性；\n")
      .append("2. 分析竞争关键词与种子关键词之间的关联性，包括相似性或互补性；\n")
      .append("3. 针对竞争关键词，讨论其流行趋势、典型应用场景和潜在竞争优势；\n")
      .append("4. 综合分析，总结竞争关键词的价值，并提供相关优化或扩展建议。\n\n")
      .append("请用专业、流畅且逻辑清晰的语言完成以上内容。");
    return sb.toString();
  }
}