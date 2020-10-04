package com.gmail.tikrai.payments.health;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class IpResolveApiHealthIndicatorTest {

  private final RestTemplate restTemplate = mock(RestTemplate.class);
  private final String ipResolveApiUrl = "ipResolveApiUrl";
  private final IpResolveApiHealthIndicator healthInd =
      new IpResolveApiHealthIndicator(restTemplate, ipResolveApiUrl);

  @Test
  void shouldReturnIpResolveApiIsUp() {
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
    when(restTemplate.getForEntity(ipResolveApiUrl, String.class)).thenReturn(response);

    Health actual = healthInd.health();

    assertThat(actual, equalTo(Health.up().build()));
    verify(restTemplate).getForEntity(ipResolveApiUrl, String.class);
  }

  @Test
  void shouldReturnIpResolveApiIsDown() {
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    when(restTemplate.getForEntity(ipResolveApiUrl, String.class)).thenReturn(response);

    Health actual = healthInd.health();

    assertThat(actual, equalTo(Health.down().build()));
    verify(restTemplate).getForEntity(ipResolveApiUrl, String.class);
  }

  @Test
  void shouldReturnIpResolveApiIsDownWhenNoConnection() {
    when(restTemplate.getForEntity(ipResolveApiUrl, String.class))
        .thenThrow(new RestClientException(""));
    Health actual = healthInd.health();

    assertThat(actual, equalTo(Health.down().build()));
    verify(restTemplate).getForEntity(ipResolveApiUrl, String.class);
  }

  @AfterEach
  void verifyMocks() {
    verifyNoMoreInteractions(restTemplate);
  }
}