package com.example.comp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class OpenAIConfig {

  @Value("${openai.api-url}")
  private String apiUrl;

  @Value("${openai.api-key}")
  private String apiKey;
}