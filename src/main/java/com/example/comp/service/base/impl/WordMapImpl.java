package com.example.comp.service.base.impl;

import com.example.comp.entity.WithWord;
import com.example.comp.service.base.inter.WordMap;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
class WordOccur{
  public int occur;
  public String word;
}

@Service
public class WordMapImpl implements WordMap {

  private HashMap<String, Integer> wordMap;
  private ArrayList<WordOccur> wordList;

  @PostConstruct
  void init(){
    wordMap = new HashMap<>();
    wordList = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/node/node.csv"))) {
      // 跳过表头
      reader.readLine();
      String line;
      int i = 0;
      while ((line = reader.readLine()) != null) {
        String[] columns = line.split(",");
        String word = columns[1];  // 第二列是单词
        int occur = Integer.parseInt(columns[2]);  // 第三列是出现次数
        // 将单词和数字添加到 HashMap 中
        wordMap.put(word, i++);
        // 同时将单词添加到 ArrayList 中
        wordList.add(new WordOccur(occur, word));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Integer getIdByWord(String word) {
    return wordMap.get(word);
  }

  @Override
  public void setWordFor(List<? extends WithWord> lis) {
    for (WithWord word : lis) {
      WordOccur wordOccur = wordList.get(word.getId());
      if (wordOccur != null) {
        word.setWord(wordOccur.word);
      }
    }
  }

  @Override
  public Integer getOccurance(int wordId) {
    WordOccur wordOccur = wordList.get(wordId);
    if (wordOccur != null) {
      return wordOccur.occur;
    }
    return null;
  }
}
