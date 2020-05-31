package com.hitop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
  final Logger logger = LoggerFactory.getLogger(AppConfig.class);
  
  private static Long timeout = 1200000l;
  
  @Bean
  public SseEmitter getSseEmitter() {
    return new SseEmitter(timeout);
  }
}