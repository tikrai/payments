package com.gmail.tikrai.payments.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class IpResolveApiHealthIndicator implements HealthIndicator {

  private final RestTemplate restTemplate;
  private final String url;

  @Autowired
  public IpResolveApiHealthIndicator(
      RestTemplate restTemplate,
      @Value("${payments.ipResolveApiUrl}") String url
  ) {
    this.restTemplate = restTemplate;
    this.url = String.format(url, "1");
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
