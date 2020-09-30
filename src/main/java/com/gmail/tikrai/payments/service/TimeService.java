package com.gmail.tikrai.payments.service;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeService {

  @Autowired
  public TimeService() {}

  public Instant now() {
    return Instant.now();
  }
}
