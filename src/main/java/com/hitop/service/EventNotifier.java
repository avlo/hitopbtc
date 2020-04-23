package com.hitop.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EventNotifier {
//  public String feed(String value);
  public SseEmitter feed();
  public void sendEvent();
}
