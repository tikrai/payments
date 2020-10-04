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

class NotificationApiHealthIndicatorTest {

  private final RestTemplate restTemplate = mock(RestTemplate.class);
  private final String notifyApiUrl = "notifyApiUrl";
  private final NotificationApiHealthIndicator healthInd =
      new NotificationApiHealthIndicator(restTemplate, notifyApiUrl);

  @Test
  void shouldReturnNotificationApiIsUp() {
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
    when(restTemplate.getForEntity(notifyApiUrl, String.class)).thenReturn(response);

    Health actual = healthInd.health();

    assertThat(actual, equalTo(Health.up().build()));
    verify(restTemplate).getForEntity(notifyApiUrl, String.class);
  }

  @Test
  void shouldReturnNotificationApiIsDown() {
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    when(restTemplate.getForEntity(notifyApiUrl, String.class)).thenReturn(response);

    Health actual = healthInd.health();

    assertThat(actual, equalTo(Health.down().build()));
    verify(restTemplate).getForEntity(notifyApiUrl, String.class);
  }

  @Test
  void shouldReturnNotificationApiIsDownWhenNoConnection() {
    when(restTemplate.getForEntity(notifyApiUrl, String.class))
        .thenThrow(new RestClientException(""));
    Health actual = healthInd.health();

    assertThat(actual, equalTo(Health.down().build()));
    verify(restTemplate).getForEntity(notifyApiUrl, String.class);
  }

  @AfterEach
  void verifyMocks() {
    verifyNoMoreInteractions(restTemplate);
  }
}