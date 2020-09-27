package com.gmail.tikrai.payments.validation.validators;

import static com.gmail.tikrai.payments.utils.Matchers.isOptionalOf;
import static org.hamcrest.MatcherAssert.assertThat;

import com.gmail.tikrai.payments.validation.Validator;
import org.junit.jupiter.api.Test;

class RegexValidatorTest {
  private final String field = "name";
  private final String regex = "TYPE[123]";
  private Validator validator;

  @Test
  void shoudValidateRegexOfNull() {
    validator = RegexValidator.of(field, null, regex);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateRegex() {
    validator = RegexValidator.of(field, "TYPE1", regex);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateRegex() {
    validator = RegexValidator.of(field, "ATYPE1", regex);
    String expected = "'name' value 'ATYPE1' is not valid";
    assertThat(validator.valid(), isOptionalOf(expected));
  }

  @Test
  void shoudFailToValidateRegexWithSuppliedMessage() {
    validator = RegexValidator.of(field, "TYPE", regex, "oops, too short");
    assertThat(validator.valid(), isOptionalOf("oops, too short"));
  }

  @Test
  void shoudFailToValidateRegexWithSuppliedMessageFormat() {
    validator = RegexValidator.of(field, "TYPE11", regex, "oops, %s value %s is not valid");
    assertThat(validator.valid(), isOptionalOf("oops, name value TYPE11 is not valid"));
  }
}
