package com.gmail.tikrai.payments.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ValidatorGroupTest {

  private final String error1 = "Error 1";
  private final String error2 = "Error 2";

  private final Validator valid = Optional::empty;
  private final Validator invalid1 = () -> Optional.of(error1);
  private final Validator invalid2 = () -> Optional.of(error2);

  @Test
  void shoudValidateGroupWithValidValidator() {
    ValidatorGroup group = ValidatorGroup.of(valid);
    assertThat(group.valid(), is(Optional.empty()));
  }

  @Test
  void shoudValidateGroupWithInValidValidator() {
    ValidatorGroup group = ValidatorGroup.of(invalid1);
    assertThat(group.valid(), is(invalid1.valid()));
  }

  @Test
  void shoudValidateGroupWithValidAndInvalidValidator() {
    ValidatorGroup group = ValidatorGroup.of(valid, invalid1);
    assertThat(group.valid(), is(invalid1.valid()));
  }

  @Test
  void shoudValidateGroupOf3Validators() {
    ValidatorGroup group = ValidatorGroup.of(valid, invalid1, invalid2);
    assertThat(group.valid(), is(invalid1.valid()));
  }

  @Test
  void shoudValidateListOfValidators() {
    ValidatorGroup group = ValidatorGroup.of(Arrays.asList(valid, invalid1));
    assertThat(group.valid(), is(invalid1.valid()));
  }
}
