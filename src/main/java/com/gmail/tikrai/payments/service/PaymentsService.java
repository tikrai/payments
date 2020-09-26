package com.gmail.tikrai.payments.service;

import com.gmail.tikrai.payments.domain.Payment;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentsService {

  private static Payment dummyPayment = new Payment(
      1,
      "TYPE1",
      BigDecimal.valueOf(1.1),
      "EUR",
      "from me",
      "to you",
      null,
      null
  );

  @Autowired
  public PaymentsService() {
  }

  public Payment findById(int id) {
    return dummyPayment.withId(id);
  }

  public Payment create(Payment request) {
    return request;
  }

  public Payment cancel(int id) {
    return dummyPayment.withId(id);
  }

  public List<Payment> findAllPending() {
    return Collections.singletonList(dummyPayment);
  }
}
