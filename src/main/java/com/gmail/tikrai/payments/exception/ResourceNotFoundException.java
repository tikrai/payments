package com.gmail.tikrai.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public static ResourceNotFoundException ofPayment(int paymentId) {
    return new ResourceNotFoundException(
        String.format("Non cancelled payment with id '%s' was not found", paymentId));
  }
}
