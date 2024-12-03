package com.example.comp.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResCodeEnum {
  Success(0, "success"),
  WordNotFound(1, "word not found");

  private final int code;
  @Getter
  private final String msg;

  @JsonValue
  public int getCode() {
    return code;
  }

  @JsonCreator
  public static ResCodeEnum valueOf(int code) {
    for (ResCodeEnum type : ResCodeEnum.values()) {
      if (type.code == code) {
        return type;
      }
    }
    return null;
  }

  public boolean isSuccess() {
    return this == Success;
  }
}