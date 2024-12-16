package com.example.comp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecoWordRes {
  int wordId;
  String word;
  List<RecoCompWord> recoCompWords;
}
