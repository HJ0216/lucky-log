package com.fortunehub.luckylog.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class RequestIdFilter implements
    Filter { // 스프링의 컨트롤러에 요청이 도달하기 전에 먼저 요청을 가로채서 특정 작업을 수행하는 역할

  private static final String REQUEST_ID_KEY = "requestId";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      String requestId = UUID.randomUUID().toString().substring(0, 8);
      MDC.put(REQUEST_ID_KEY, requestId);

      chain.doFilter(request, response);
    } finally {
      MDC.remove(REQUEST_ID_KEY);
    }
  }
}
