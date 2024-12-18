package com.example.comp.entity.req;

import com.example.comp.entity.WordScore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RelatedWord {
  private String query;
  @JsonProperty("related_keywords")
  private List<WordScore> relatedKeywords;
}
