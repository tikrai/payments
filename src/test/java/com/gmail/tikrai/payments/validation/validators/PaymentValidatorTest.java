package com.gmail.tikrai.payments.validation.validators;

import static com.gmail.tikrai.payments.utils.Matchers.isOptionalOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.fixture.PaymentRequestFixture;
import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.validation.Validator;
import org.junit.jupiter.api.Test;

class PaymentValidatorTest {
  private final PaymentRequestFixture requestFixture = Fixture.paymentRequest().bicCode(null);
  private Validator validator;
  private PaymentRequest paymentRequest;

  @Test
  void shoudValidateType1Payment() {
    paymentRequest = requestFixture.type("TYPE1").build();
    validator = PaymentValidator.ofType1(paymentRequest);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateType1PaymentIfPaymentTypeIsOther() {
    paymentRequest = requestFixture.type("TYPE2").currency("LTL").build();
    validator = PaymentValidator.ofType1(paymentRequest);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateType1PaymentIfCurrencyIsNotEur() {
    paymentRequest = requestFixture.type("TYPE1").currency("LTL").build();
    validator = PaymentValidator.ofType1(paymentRequest);
    assertThat(validator.valid(), isOptionalOf("'currency' must be 'EUR' for TYPE1 payment"));
  }

  @Test
  void shoudFailToValidateType1PaymentIfBicCodeIsNotNull() {
    paymentRequest = requestFixture.type("TYPE1").bicCode("BIC").build();
    validator = PaymentValidator.ofType1(paymentRequest);
    assertThat(validator.valid(), isOptionalOf("'bic_code' not available for TYPE1 payment"));
  }

  @Test
  void shoudFailToValidateType1PaymentIfDetailsIsNull() {
    paymentRequest = requestFixture.type("TYPE1").details(null).build();
    validator = PaymentValidator.ofType1(paymentRequest);
    assertThat(validator.valid(), isOptionalOf("'details' cannot be null for TYPE1 payment"));
  }


  @Test
  void shoudValidateType2Payment() {
    paymentRequest = requestFixture.type("TYPE2").currency("USD").build();
    validator = PaymentValidator.ofType2(paymentRequest);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateType2PaymentIfPaymentTypeIsOther() {
    paymentRequest = requestFixture.type("TYPE1").currency("LTL").build();
    validator = PaymentValidator.ofType2(paymentRequest);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateType2PaymentIfCurrencyIsNotUSD() {
    paymentRequest = requestFixture.type("TYPE2").currency("LTL").build();
    validator = PaymentValidator.ofType2(paymentRequest);
    assertThat(validator.valid(), isOptionalOf("'currency' must be 'USD' for TYPE2 payment"));
  }

  @Test
  void shoudFailToValidateType2PaymentIfBicCodeIsNotNull() {
    paymentRequest = requestFixture.type("TYPE2").currency("USD").bicCode("BIC").build();
    validator = PaymentValidator.ofType2(paymentRequest);
    assertThat(validator.valid(), isOptionalOf("'bic_code' not available for TYPE2 payment"));
  }



  @Test
  void shoudValidateType3Payment() {
    paymentRequest = requestFixture.type("TYPE3").bicCode("BIC").details(null).build();
    validator = PaymentValidator.ofType3(paymentRequest);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudValidateType3PaymentIfPaymentTypeIsOther() {
    paymentRequest = requestFixture.type("TYPE1").currency("LTL").build();
    validator = PaymentValidator.ofType3(paymentRequest);
    assertThat(validator.valid(), isOptionalOf(null));
  }

  @Test
  void shoudFailToValidateType3PaymentIfBicCodeIsNull() {
    paymentRequest = requestFixture.type("TYPE3").details(null).bicCode(null).build();
    validator = PaymentValidator.ofType3(paymentRequest);
    assertThat(validator.valid(), isOptionalOf("'bic_code' cannot be null for TYPE3 payment"));
  }

  @Test
  void shoudFailToValidateType3PaymentIfDetailsIsNotNull() {
    paymentRequest =
        requestFixture.type("TYPE3").currency("USD").bicCode("BIC").details("details").build();
    validator = PaymentValidator.ofType3(paymentRequest);
    assertThat(validator.valid(), isOptionalOf("'details' not available for TYPE3 payment"));
  }
}
