package com.example.comp.service.ai;

import java.io.IOException;

public interface AICompleter {
  String complete(String prompt, String input) throws  IOException;
}
