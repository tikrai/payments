package com.gmail.tikrai.payments.validation;

import com.gmail.tikrai.payments.exception.ValidationException;
import java.util.Optional;

public interface Validator {
  Optional<String> valid();

  default void validate() {
    valid().ifPresent(s -> { throw new ValidationException(s); });
  }
}
