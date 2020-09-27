package com.gmail.tikrai.payments.request;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import org.junit.jupiter.api.Test;

class PaymentRequestTest {

  private final ObjectMapper mapper = Fixture.mapper();
  private final String paymentJson = "{"
      + "\"type\":\"TYPE1\",\"amount\":10.01,\"currency\":\"EUR\","
      + "\"debtor_iban\":\"LT0001\",\"creditor_iban\":\"LT9999\",\"bic_code\":\"AGBLLT2X\""
      + "}";
  private final PaymentRequest paymentRequest = Fixture.paymentRequest().build();

  @Test
  void shouldSerializePaymentRequest() throws JsonProcessingException {
    String serialized = mapper.writeValueAsString(paymentRequest);
    assertThat(serialized, equalTo(paymentJson));
  }

  @Test
  void shouldDeserializePaymentRequest() throws JsonProcessingException {
    PaymentRequest deserialized = mapper.readValue(paymentJson, PaymentRequest.class);
    assertThat(deserialized, equalTo(paymentRequest));
  }
}
