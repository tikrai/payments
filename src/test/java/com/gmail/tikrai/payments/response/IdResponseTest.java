package com.gmail.tikrai.payments.response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import org.junit.jupiter.api.Test;

class IdResponseTest {  private final ObjectMapper mapper = Fixture.mapper();
  private final String responseJson = "{\"id\":1}";
  private final IdResponse response = new IdResponse(1);

  @Test
  void shouldSerializeIpApiResponse() throws JsonProcessingException {
    String serialized = mapper.writeValueAsString(response);
    assertThat(serialized, equalTo(responseJson));
  }

  @Test
  void shouldDeserializeIpApiResponse() throws JsonProcessingException {
    IdResponse deserialized = mapper.readValue(responseJson, IdResponse.class);
    assertThat(deserialized, equalTo(response));
  }
}
