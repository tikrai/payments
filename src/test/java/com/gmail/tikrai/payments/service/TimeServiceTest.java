package com.gmail.tikrai.payments.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TimeServiceTest {
  private final TimeService timeService = new TimeService();

  @Test
  void shouldVerifyTimeService() {
    Instant actual = timeService.now();
    Instant expected = Instant.now();

    Duration difference = Duration.between(actual, expected);
    assertThat(difference, lessThanOrEqualTo(Duration.ofMillis(1)));
    assertThat(difference, not(lessThan(Duration.ofMillis(0))));
  }
}
