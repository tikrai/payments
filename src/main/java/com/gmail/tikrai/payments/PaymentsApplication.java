package com.gmail.tikrai.payments;

import com.gmail.tikrai.payments.util.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Generated
@SpringBootApplication
@EnableAsync
public class PaymentsApplication {

  public static void main(String[] args) {
    SpringApplication.run(PaymentsApplication.class, args);
  }
}
