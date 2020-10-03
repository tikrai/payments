package com.gmail.tikrai.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PaymentsApplication {

  public static void main(String[] args) {
    SpringApplication.run(PaymentsApplication.class, args);
  }
}
