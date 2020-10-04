package com.gmail.tikrai.payments.response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PaymentResponseTest {
  private final ObjectMapper mapper = Fixture.mapper();
  private final String responseJson = "{"
      + "\"id\":0,\"created\":\"1973-11-29T21:33:09Z\",\"type\":\"TYPE1\",\"amount\":10.01,"
      + "\"currency\":\"EUR\",\"debtor_iban\":\"LT0001\",\"creditor_iban\":\"LT9999\","
      + "\"bic_code\":\"AGBLLT2X\",\"details\":\"details\"}";
  private final PaymentResponse response =
      PaymentResponse.of(Fixture.payment().created(Instant.ofEpochSecond(123456789)).build());

  @Test
  void shouldSerializePaymentResponse() throws JsonProcessingException {
    String serialized = mapper.writeValueAsString(response);
    assertThat(serialized, equalTo(responseJson));
  }

  @Test
  void shouldDeserializePaymentResponse() throws JsonProcessingException {
    PaymentResponse deserialized = mapper.readValue(responseJson, PaymentResponse.class);
    assertThat(deserialized, equalTo(response));
  }
}
