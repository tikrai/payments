package com.gmail.tikrai.payments.validation.validators;

import static com.gmail.tikrai.payments.utils.Matchers.isOptionalOf;
import static org.hamcrest.MatcherAssert.assertThat;

import com.gmail.tikrai.payments.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DecimalValidatorTest {

  private final String field = "name";
  BigDecimal valid = BigDecimal.valueOf(1.19);
  BigDecimal invalid = BigDecimal.valueOf(1.191);
  private Validator validator;

  @Test
  void shoudValidateMaxDecimalDigitsOfNull() {
    validator = DecimalValidator.maxDecimals(field, null, 2);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateMaxDecimalDigits() {
    validator = DecimalValidator.maxDecimals(field, valid, 2);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateMaxDecimalDigits() {
    validator = DecimalValidator.maxDecimals(field, invalid, 2);
    String expected = "'name' value '1.191' must be rounded to 2 decimal digits";
    assertThat(validator.valid(), isOptionalOf(expected));
  }

  @Test
  void shoudFailToValidateMaxDecimalDigitsWithSuppliedMessage() {
    validator = DecimalValidator.maxDecimals(field, invalid, 2, "oops, too short");
    assertThat(validator.valid(), isOptionalOf("oops, too short"));
  }

  @Test
  void shoudFailToValidateMaxDecimalDigitsWithSuppliedMessageFormat() {
    validator = DecimalValidator.maxDecimals(field, invalid, 2, "oops, %s value %s is not valid");
    String expected = String.format("oops, name value %s is not valid", invalid);
    assertThat(validator.valid(), isOptionalOf(expected));
  }
}
