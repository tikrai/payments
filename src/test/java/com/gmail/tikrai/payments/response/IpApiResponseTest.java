package com.gmail.tikrai.payments.response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import org.junit.jupiter.api.Test;

class IpApiResponseTest {  private final ObjectMapper mapper = Fixture.mapper();
  private final String responseJson = "{\"country\":\"Paylandia\"}";
  private final IpApiResponse response = new IpApiResponse("Paylandia");

  @Test
  void shouldSerializeIpApiResponse() throws JsonProcessingException {
    String serialized = mapper.writeValueAsString(response);
    assertThat(serialized, equalTo(responseJson));
  }

  @Test
  void shouldDeserializeIpApiResponse() throws JsonProcessingException {
    IpApiResponse deserialized = mapper.readValue(responseJson, IpApiResponse.class);
    assertThat(deserialized, equalTo(response));
  }
}
