package com.gmail.tikrai.payments.response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PaymentCancelFeeResponseTest {
  private final ObjectMapper mapper = Fixture.mapper();
  private final String responseJson = "{\"id\":1,\"cancel_possible\":true,\"price\":1}";
  private final PaymentCancelFeeResponse response =
      new PaymentCancelFeeResponse(1, true, BigDecimal.ONE);

  @Test
  void shouldSerializePaymentCancelFeeResponse() throws JsonProcessingException {
    String serialized = mapper.writeValueAsString(response);
    assertThat(serialized, equalTo(responseJson));
  }

  @Test
  void shouldDeserializePaymentCancelFeeResponse() throws JsonProcessingException {
    PaymentCancelFeeResponse deserialized =
        mapper.readValue(responseJson, PaymentCancelFeeResponse.class);
    assertThat(deserialized, equalTo(response));
  }
}
