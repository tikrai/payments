package com.gmail.tikrai.payments.validation.validators;

import com.gmail.tikrai.payments.validation.Validator;
import java.util.Optional;

public class RegexValidator implements Validator {

  private static final String DEFAULT_MESSAGE = "'%s' value '%s' is not valid";
  private final String message;

  private RegexValidator(String message) {
    this.message = message;
  }

  public static RegexValidator of(String field, String value, String regex, String message) {
    return new RegexValidator(
        value == null || value.matches(regex) ? null : String.format(message, field, value)
    );
  }

  public static RegexValidator of(String field, String value, String regex) {
    return of(field, value, regex, DEFAULT_MESSAGE);
  }

  @Override
  public Optional<String> valid() {
    return Optional.ofNullable(message);
  }
}
