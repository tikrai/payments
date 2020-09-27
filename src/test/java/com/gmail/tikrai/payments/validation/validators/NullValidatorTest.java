package com.gmail.tikrai.payments.validation.validators;

import static com.gmail.tikrai.payments.utils.Matchers.isOptionalOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.gmail.tikrai.payments.validation.Validator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NullValidatorTest {
  private Validator validator;

  @Test
  void shoudValidateNonNullString() {
    validator = NullValidator.not("name", new Object());
    assertThat(validator.valid(), equalTo(Optional.empty()));
  }

  @Test
  void shoudFailToValidate() {
    validator = NullValidator.not("name", null);
    assertThat(validator.valid(), isOptionalOf("'name' cannot be null"));
  }

  @Test
  void shoudFailToValidateWithSuppliedMessage() {
    validator = NullValidator.not("name", null, "oops, null");
    assertThat(validator.valid(), isOptionalOf("oops, null"));
  }

  @Test
  void shoudFailToValidateWithSuppliedMessageFormat() {
    validator = NullValidator.not("name", null, "oops, %s is null");
    assertThat(validator.valid(), isOptionalOf("oops, name is null"));
  }

  @Test
  void shoudValidateMinNulls() {
    validator = NullValidator.min(new Object[]{new Object(), null}, 1);
    assertThat(validator.valid(), equalTo(Optional.empty()));
  }

  @Test
  void shoudFailToValidateMinNulls() {
    validator = NullValidator.min(new Object[]{new Object(), new Object()}, 1);
    String expected = "Number of null objects must be greater than or equal to 1";
    assertThat(validator.valid(), isOptionalOf(expected));
  }

  @Test
  void shoudFailToValidateMinNullsWithSuppliedMessage() {
    validator = NullValidator.min(new Object[]{new Object()}, 1, "oops, not enough nulls");
    assertThat(validator.valid(), isOptionalOf("oops, not enough nulls"));
  }

  @Test
  void shoudFailToValidateMinNullsWithSuppliedMessageFormat() {
    validator = NullValidator.min(new Object[]{}, 1, "oops, required at least %s null");
    assertThat(validator.valid(), isOptionalOf("oops, required at least 1 null"));
  }
}
