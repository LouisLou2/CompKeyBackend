package com.example.comp.service.specific.impl;


import com.example.comp.service.specific.RequestInfoExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestInfoExtractorImpl implements RequestInfoExtractor {

  @Override
  public String getIpv4(HttpServletRequest req) {
    String ip = req.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = req.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = req.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = req.getRemoteAddr();
    }
    // 如果有多个 IP 地址（通过反向代理转发），则取第一个
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0];
    }
    return ip;
  }
}