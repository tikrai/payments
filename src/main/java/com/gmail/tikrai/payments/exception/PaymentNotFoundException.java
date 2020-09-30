package com.gmail.tikrai.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PaymentNotFoundException extends RuntimeException {

  public PaymentNotFoundException(int paymentId) {
    super(String.format("Payment with id '%s' was not found", paymentId));
  }
}
