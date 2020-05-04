package com.hitop.controller;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ReceiptListener {
  void displayReceiptSse();
}
