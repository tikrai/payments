package com.gmail.tikrai.payments.repository;

import static com.gmail.tikrai.payments.domain.Payment.Currency.EUR;
import static com.gmail.tikrai.payments.domain.Payment.Type.TYPE1;

import com.gmail.tikrai.payments.domain.Payment;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentsRepository {

  private static Payment dummyPayment1 = new Payment(
      1,
      TYPE1,
      BigDecimal.valueOf(1.1),
      EUR,
      "from me",
      "to you",
      null,
      null,
      null,
      null
  );
  private static Payment dummyPayment2 = dummyPayment1.withId(2);

  @Autowired
  public PaymentsRepository() {}

  public List<Payment> findAllPending() {
    return Arrays.asList(dummyPayment1, dummyPayment2);
  }

  public Payment findById(int id) {
    return dummyPayment1.withId(id);
  }

  public Payment create(Payment payment) {
    return payment.withId(1);
  }

  public Payment cancel(int id) {
    return dummyPayment1.withId(id);
  }
}
