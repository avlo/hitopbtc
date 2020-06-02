package com.hitop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.hitop")
@SpringBootApplication
public class Application {
  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
