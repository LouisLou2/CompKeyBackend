package com.example.comp.service.specific;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestInfoExtractor {
  String getIpv4(HttpServletRequest req);
}
