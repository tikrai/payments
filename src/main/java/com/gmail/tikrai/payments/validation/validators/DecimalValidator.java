package com.gmail.tikrai.payments.validation.validators;

import static java.math.BigDecimal.ROUND_UNNECESSARY;

import com.gmail.tikrai.payments.validation.Validator;
import java.math.BigDecimal;
import java.util.Optional;

public class DecimalValidator implements Validator {

  private static final String DEFAULT_MESSAGE =
      "'%s' value '%s' must be rounded to %s decimal digits";
  private final String message;

  private DecimalValidator(String message) {
    this.message = message;
  }

  public static DecimalValidator maxDecimals(
      String field,
      BigDecimal value,
      int maxDecimals,
      String message
  ) {
    if (value == null) {
      return new DecimalValidator(null);
    }
    try {
      value = value.setScale(maxDecimals, ROUND_UNNECESSARY);
      return new DecimalValidator(null);
    } catch (ArithmeticException e) {
      return new DecimalValidator(String.format(message, field, value, maxDecimals));
    }
  }

  public static DecimalValidator maxDecimals(String field, BigDecimal value, int maxDecimals) {
    return maxDecimals(field, value, maxDecimals, DEFAULT_MESSAGE);
  }

  @Override
  public Optional<String> valid() {
    return Optional.ofNullable(message);
  }
}
