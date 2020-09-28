package com.gmail.tikrai.payments.validation.validators;

import com.gmail.tikrai.payments.validation.Validator;
import java.util.Optional;

public class SizeValidator implements Validator {

  private static final String ERROR_MAX = "'%s' must be less than or equal to %s";
  private static final String ERROR_MIN = "'%s' must be greater than or equal to %s";
  private static final String ERROR_RANGE = "'%s' must be between %s and %s";
  private static final String ERROR_LEN_MAX = "'%s' length must be less than or equal to %s";
  private static final String ERROR_LEN_MIN = "'%s' length must be greater than or equal to %s";
  private static final String ERROR_LEN_RANGE = "'%s' length must be between %s and %s";

  private final String message;

  private SizeValidator(String message) {
    this.message = message;
  }

  public static <T extends Comparable<T>> SizeValidator min(
      String field, T value, T min, String message
  ) {
    return new SizeValidator(
        value != null && min != null && value.compareTo(min) < 0
            ? String.format(message, field, min)
            : null);
  }

  public static <T extends Comparable<T>> SizeValidator min(String field, T value, T min) {
    return min(field, value, min, ERROR_MIN);
  }

  public static SizeValidator min(String field, String value, int min, String message) {
    return new SizeValidator(
        value != null && value.length() < min
            ? String.format(message, field, min)
            : null);
  }

  public static SizeValidator min(String field, String value, int min) {
    return min(field, value, min, ERROR_LEN_MIN);
  }


  public static <T extends Comparable<T>> SizeValidator max(
      String field, T value, T max, String message
  ) {
    return new SizeValidator(
        value != null && value.compareTo(max) > 0
            ? String.format(message, field, max)
            : null);
  }

  public static <T extends Comparable<T>> SizeValidator max(String field, T value, T max) {
    return max(field, value, max, ERROR_MAX);
  }

  public static SizeValidator max(String field, String value, int max, String message) {
    return new SizeValidator(
        value != null && value.length() > max
            ? String.format(message, field, max)
            : null);
  }

  public static SizeValidator max(String field, String value, int max) {
    return max(field, value, max, ERROR_LEN_MAX);
  }


  public static <T extends Comparable<T>> SizeValidator range(
      String field, T value, T min, T max, String message
  ) {
    return new SizeValidator(
        value != null && (value.compareTo(min) < 0 || value.compareTo(max) > 0)
            ? String.format(message, field, min, max)
            : null);
  }

  public static <T extends Comparable<T>> SizeValidator range(String field, T value, T min, T max) {
    return range(field, value, min, max, ERROR_RANGE);
  }

  public static SizeValidator range(String field, String value, int min, int max, String message) {
    return new SizeValidator(
        value != null && (value.length() < min || value.length() > max)
            ? String.format(message, field, min, max)
            : null);
  }

  public static SizeValidator range(String field, String value, int min, int max) {
    return range(field, value, min, max, ERROR_LEN_RANGE);
  }

  @Override
  public Optional<String> valid() {
    return Optional.ofNullable(message);
  }
}
