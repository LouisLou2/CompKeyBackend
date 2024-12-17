package com.example.comp.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ModelOutputParser {
  String modelOutputPrimaryChoice(String modelOutput) throws JsonProcessingException;
}
