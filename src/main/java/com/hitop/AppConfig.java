package com.hitop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
  @Bean
  public SseEmitter getSseEmitter() {
    return new SseEmitter();
  }
}