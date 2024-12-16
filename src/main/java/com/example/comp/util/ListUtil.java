package com.example.comp.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
  public static int numOfNullInTail(List<?> list) {
    int count = 0;
    for (int i = list.size() - 1; i >= 0; i--) {
      if (list.get(i) == null) {
        count++;
      } else {
        break;
      }
    }
    return count;
  }

  public static <E> List<E> safeSubList(int offset, int num, List<E> list) {
    assert offset >= 0 && num >= 0;
    assert list != null;
    if (offset >= list.size()) return new ArrayList<>();
    int end = offset + num;
    if (end > list.size()) {
      end = list.size();
    }
    return list.subList(offset, end);
  }
}
