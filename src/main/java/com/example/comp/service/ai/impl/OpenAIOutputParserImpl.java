package com.example.comp.service.ai.impl;

import com.example.comp.service.ai.ModelOutputParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service("OpenAIOutputParserImpl")
public class OpenAIOutputParserImpl implements ModelOutputParser{

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String modelOutputPrimaryChoice(String modelOutput) throws JsonProcessingException {
    assert modelOutput != null;
    // 解析响应
    JsonNode jsonResponse = objectMapper.readTree(modelOutput);
    // 检查是否包含错误信息
    if (jsonResponse.has("error")) {
      System.out.println("OpenAI API Error: " + jsonResponse.get("error").get("message").asText());
      return null;
    }
    // 获取内容
    JsonNode choicesNode = jsonResponse.get("choices");
    if (choicesNode != null && choicesNode.isArray() && !choicesNode.isEmpty()) {
      JsonNode messageNode = choicesNode.get(0).get("message");
      if (messageNode != null && messageNode.has("content")) {
        return messageNode.get("content").asText();
      }
    }
    return null;
  }
}