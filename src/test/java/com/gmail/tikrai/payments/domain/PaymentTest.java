package com.gmail.tikrai.payments.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PaymentTest {

  private final ObjectMapper mapper = Fixture.mapper();
  private final String paymentJson = "{"
      + "\"id\":12,\"created\":\"-1000000000-01-01T00:00:00Z\","
      + "\"cancelled\":\"+1000000000-12-31T23:59:59.999999999Z\","
      + "\"type\":\"TYPE1\",\"amount\":10.01,\"currency\":\"EUR\",\"debtor_iban\":\"LT0001\","
      + "\"creditor_iban\":\"LT9999\",\"bic_code\":\"AGBLLT2X\",\"details\":\"details\""
      + ",\"ip_address\":\"127.0.0.1\"}";
  private final Payment payment = Fixture.payment()
      .created(Instant.MIN)
      .cancelled(Instant.MAX)
      .build()
      .withId(12);

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
