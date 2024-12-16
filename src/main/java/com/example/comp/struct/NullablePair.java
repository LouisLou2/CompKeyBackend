package com.example.comp.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NullablePair<T,K> {
  private T first;
  private K second;

  public static <T,K> NullablePair<T,K> of(T first) {
    return new NullablePair<>(first,null);
  }
}