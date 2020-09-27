package com.gmail.tikrai.payments.validation.validators;

import static com.gmail.tikrai.payments.utils.Matchers.isOptionalOf;
import static org.hamcrest.MatcherAssert.assertThat;

import com.gmail.tikrai.payments.validation.Validator;
import org.junit.jupiter.api.Test;

class SizeValidatorTest {
  private final String string = "string";
  private final int six = 6;
  private Validator validator;

  @Test
  void shoudValidateMinSizeOfNull() {
    validator = SizeValidator.min("count", (Integer) null, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateMinSize() {
    validator = SizeValidator.min("count", six, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateMinSize() {
    validator = SizeValidator.min("count", six, 7);
    String expected = "'count' must be greater than or equal to 7";
    assertThat(validator.valid(), isOptionalOf(expected));
    assertThat(validator.valid(), isOptionalOf(expected));
  }

  @Test
  void shoudFailToValidateMinSizeWithSuppliedMessage() {
    validator = SizeValidator.min("count", six, 7, "oops, too small");
    assertThat(validator.valid(), isOptionalOf("oops, too small"));
  }

  @Test
  void shoudFailToValidateMinSizeWithSuppliedMessageFormat() {
    validator = SizeValidator.min("count", six, 7, "oops, %s is smaller than %s");
    assertThat(validator.valid(), isOptionalOf("oops, count is smaller than 7"));
  }

  @Test
  void shoudValidateMinStringLengthOfNull() {
    validator = SizeValidator.min("name", null, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateMinStringLength() {
    validator = SizeValidator.min("name", string, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateMinStringLength() {
    validator = SizeValidator.min("name", string, 7);
    String expected = "'name' length must be greater than or equal to 7";
    assertThat(validator.valid(), isOptionalOf(expected));
  }

  @Test
  void shoudFailToValidateMinStringLengthWithSuppliedMessage() {
    validator = SizeValidator.min("name", string, 7, "oops, too short");
    assertThat(validator.valid(), isOptionalOf("oops, too short"));
  }

  @Test
  void shoudFailToValidateMinStringLengthWithSuppliedMessageFormat() {
    validator = SizeValidator.min("name", string, 7, "oops, %s is shorter than %s");
    assertThat(validator.valid(), isOptionalOf("oops, name is shorter than 7"));
  }



  @Test
  void shoudValidateMaxSizeOfNull() {
    validator = SizeValidator.max("count", (Integer) null, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateMaxSize() {
    validator = SizeValidator.max("count", six, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateMaxSize() {
    validator = SizeValidator.max("count", six, 5);
    assertThat(validator.valid(), isOptionalOf("'count' must be less than or equal to 5"));
  }

  @Test
  void shoudFailToValidateMaxSizeWithSuppliedMessage() {
    validator = SizeValidator.max("count", six, 5, "oops, too big");
    assertThat(validator.valid(), isOptionalOf("oops, too big"));
  }

  @Test
  void shoudFailToValidateMaxSizeWithSuppliedMessageFormat() {
    validator = SizeValidator.max("count", six, 5, "oops, %s is bigger than %s");
    assertThat(validator.valid(), isOptionalOf("oops, count is bigger than 5"));
  }

  @Test
  void shoudValidateMaxStringLengthOfNull() {
    validator = SizeValidator.max("name", null, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateMaxStringLength() {
    validator = SizeValidator.max("name", string, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateMaxStringLength() {
    validator = SizeValidator.max("name", string, 5);
    String expected = "'name' length must be less than or equal to 5";
    assertThat(validator.valid(), isOptionalOf(expected));
  }

  @Test
  void shoudFailToValidateMaxStringLengthWithSuppliedMessage() {
    validator = SizeValidator.max("name", string, 5, "oops, too long");
    assertThat(validator.valid(), isOptionalOf("oops, too long"));
  }

  @Test
  void shoudFailToValidateMaxStringLengthWithSuppliedMessageFormat() {
    validator = SizeValidator.max("name", string, 5, "oops, %s is longer than %s");
    assertThat(validator.valid(), isOptionalOf("oops, name is longer than 5"));
  }


  @Test
  void shoudValidateRangeOfNull() {
    validator = SizeValidator.range("count", (Integer) null, 6, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateRange() {
    validator = SizeValidator.range("count", six, 6, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateBiggerRange() {
    validator = SizeValidator.range("count", six, 7, 7);
    assertThat(validator.valid(), isOptionalOf("'count' must be between 7 and 7"));
  }

  @Test
  void shoudFailToValidateSmallerRange() {
    validator = SizeValidator.range("count", six, 5, 5);
    assertThat(validator.valid(), isOptionalOf("'count' must be between 5 and 5"));
  }

  @Test
  void shoudFailToValidateRangeWithSuppliedMessage() {
    validator = SizeValidator.range("count", six, 7, 7, "oops, too small");
    assertThat(validator.valid(), isOptionalOf("oops, too small"));
  }

  @Test
  void shoudFailToValidateRangeWithSuppliedMessageFormat() {
    validator = SizeValidator.range("count", six, 7, 7, "oops, %s is out of %s-%s range");
    assertThat(validator.valid(), isOptionalOf("oops, count is out of 7-7 range"));
  }

  @Test
  void shoudValidateStringLengthRangeOfNull() {
    validator = SizeValidator.range("name", null, 6, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateStringLengthRange() {
    validator = SizeValidator.range("name", string, 6, 6);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateBiggerStringLengthRange() {
    validator = SizeValidator.range("name", string, 7, 7);
    assertThat(validator.valid(), isOptionalOf("'name' length must be between 7 and 7"));
  }

  @Test
  void shoudFailToValidateSmallerStringLengthRange() {
    validator = SizeValidator.range("name", string, 5, 5);
    assertThat(validator.valid(), isOptionalOf("'name' length must be between 5 and 5"));
  }

  @Test
  void shoudFailToValidateStringLengthRangeWithSuppliedMessage() {
    validator = SizeValidator.range("name", string, 5, 5, "oops, out of range");
    assertThat(validator.valid(), isOptionalOf("oops, out of range"));
  }

  @Test
  void shoudFailToValidateStringLengthRangeWithSuppliedMessageFormat() {
    validator = SizeValidator.range("name", string, 5, 5, "oops, %s length is out of %s-%s range");
    assertThat(validator.valid(), isOptionalOf("oops, name length is out of 5-5 range"));
  }
}
