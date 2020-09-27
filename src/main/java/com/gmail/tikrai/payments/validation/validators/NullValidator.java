package com.gmail.tikrai.payments.validation.validators;

import com.gmail.tikrai.payments.validation.Validator;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class NullValidator implements Validator {

  private static final String DEFAULT_MESSAGE = "'%s' cannot be null";
  private static final String MIN_MESSAGE =
      "Number of null objects must be greater than or equal to %s";

  private final String message;

  private NullValidator(String message) {
    this.message = message;
  }

  public static NullValidator not(String field, Object object, String message) {
    return new NullValidator(object == null ? String.format(message, field) : null);
  }

  public static NullValidator not(String field, Object object) {
    return not(field, object, DEFAULT_MESSAGE);
  }

  public static NullValidator min(Object[] objects, int min, String message) {
    return new NullValidator(
        Arrays.stream(objects).filter(Objects::isNull).count() < min
            ? String.format(message, min)
            : null);
  }

  public static NullValidator min(Object[] objects, int min) {
    return min(objects, min, MIN_MESSAGE);
  }

  @Override
  public Optional<String> valid() {
    return Optional.ofNullable(message);
  }
}
