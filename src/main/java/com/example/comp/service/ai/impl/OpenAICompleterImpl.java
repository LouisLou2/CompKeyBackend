package com.example.comp.service.ai.impl;

import com.example.comp.config.OpenAIConfig;
import com.example.comp.service.ai.AICompleter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@Service(value="OpenAICompleterImpl")
public class OpenAICompleterImpl implements AICompleter {

  @Resource
  private OpenAIConfig openAIConfig;

  @Override
  public String complete(String prompt,String input) throws IOException {
    String apiUrl = openAIConfig.getApiUrl() + "/chat/completions";
    String apiKey = openAIConfig.getApiKey();

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(apiUrl);

    // 构造请求体
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(Map.of(
        "model", "gpt-4o-mini",
        "messages", List.of(
            Map.of("role", "system", "content", prompt),
            Map.of("role", "user", "content", input)
        )
    ));
    // 打印调试信息
    System.out.println("Request Body: " + requestBody);
    // 设置请求头
    httpPost.setHeader("Authorization", "Bearer " + apiKey);
    httpPost.setHeader("Content-Type", "application/json");
    // 设置请求体
    httpPost.setEntity(new StringEntity(requestBody, "UTF-8"));
    // 执行请求
    CloseableHttpResponse response = httpClient.execute(httpPost);
    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuilder responseBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      responseBuilder.append(line);
    }
    return responseBuilder.toString();
  }
}