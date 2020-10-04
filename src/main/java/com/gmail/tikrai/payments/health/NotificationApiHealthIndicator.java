package com.gmail.tikrai.payments.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationApiHealthIndicator implements HealthIndicator {

  private final RestTemplate restTemplate;
  private final String url;

  @Autowired
  public NotificationApiHealthIndicator(
      RestTemplate restTemplate,
      @Value("${payments.notifyApi}") String url
  ) {
    this.restTemplate = restTemplate;
    this.url = url;
  }

  @Override
  public Health health() {
    try {
      return restTemplate.getForEntity(url, String.class).getStatusCode().is2xxSuccessful()
          ? Health.up().build()
          : Health.down().build();
    } catch (RestClientException e) {
      return Health.down().build();
    }
  }
}
