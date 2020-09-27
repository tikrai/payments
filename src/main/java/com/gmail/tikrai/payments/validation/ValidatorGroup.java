package com.gmail.tikrai.payments.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ValidatorGroup implements Validator {

  private final List<Validator> validators;

  private ValidatorGroup(List<Validator> validators) {
    this.validators = validators;
  }

  public static ValidatorGroup of(List<Validator> validators) {
    return new ValidatorGroup(validators);
  }

  public static ValidatorGroup of(Validator... validators) {
    return new ValidatorGroup(Arrays.asList(validators));
  }

  @Override
  public Optional<String> valid() {
    return validators.stream()
        .filter(Objects::nonNull)
        .map(Validator::valid)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }
}
