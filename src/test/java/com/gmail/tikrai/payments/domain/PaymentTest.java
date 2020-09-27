package com.gmail.tikrai.payments.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import org.junit.jupiter.api.Test;

class PaymentTest {

  private final ObjectMapper mapper = Fixture.mapper();
  private final String paymentJson = "{"
      + "\"id\":0,\"cancelled\":false,\"type\":\"TYPE1\",\"amount\":10.01,"
      + "\"currency\":\"EUR\",\"debtor_iban\":\"LT0001\",\"creditor_iban\":\"LT9999\","
      + "\"bic_code\":\"AGBLLT2X\"}";
  private final Payment payment = Fixture.payment().build();

  @Test
  void shouldSerializePayment() throws JsonProcessingException {
    String serialized = mapper.writeValueAsString(payment);
    assertThat(serialized, equalTo(paymentJson));
  }

  @Test
  void shouldDeserializePayment() throws JsonProcessingException {
    Payment deserialized = mapper.readValue(paymentJson, Payment.class);
    assertThat(deserialized, equalTo(payment));
  }
}
