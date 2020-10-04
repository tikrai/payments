package com.gmail.tikrai.payments.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CancelFeeTest {
  private final ObjectMapper mapper = Fixture.mapper();
  private final String responseJson =
      "{\"id\":1,\"cancel_possible\":true,\"fee\":1,\"time\":\"2009-02-13T23:31:30Z\"}";
  private final CancelFee response =
      new CancelFee(1, true, BigDecimal.ONE, Instant.ofEpochSecond(1234567890));

  @Test
  void shouldSerializePaymentCancelFeeResponse() throws JsonProcessingException {
    String serialized = mapper.writeValueAsString(response);
    assertThat(serialized, equalTo(responseJson));
  }

  @Test
  void shouldDeserializePaymentCancelFeeResponse() throws JsonProcessingException {
    CancelFee deserialized = mapper.readValue(responseJson, CancelFee.class);
    assertThat(deserialized, equalTo(response));
  }
}
