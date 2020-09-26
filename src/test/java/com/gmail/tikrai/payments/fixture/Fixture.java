package com.gmail.tikrai.payments.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.config.ObjectMapperConfiguration;

public final class Fixture {

  public static ObjectMapper mapper() {
    return new ObjectMapperConfiguration().mapper();
  }

  public static PaymentFixture payment() {
    return new PaymentFixture();
  }

  public static PaymentRequestFixture paymentRequest() {
    return new PaymentRequestFixture();
  }
}
