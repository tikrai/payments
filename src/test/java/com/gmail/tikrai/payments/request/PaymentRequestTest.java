package com.gmail.tikrai.payments.request;

import static com.gmail.tikrai.payments.utils.Matchers.isOptionalOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.fixture.PaymentRequestFixture;
import org.junit.jupiter.api.Test;

class PaymentRequestTest {

  private final ObjectMapper mapper = Fixture.mapper();
  private final String paymentJson = "{"
      + "\"type\":\"TYPE1\",\"amount\":10.01,\"currency\":\"EUR\",\"debtor_iban\":\"LT0001\","
      + "\"creditor_iban\":\"LT9999\",\"bic_code\":\"AGBLLT2X\",\"details\":\"details\""
      + "}";
  private final PaymentRequestFixture requestFixture = Fixture.paymentRequest().bicCode(null);

  private PaymentRequest paymentRequest;

  @Test
  void shouldSerializePaymentRequest() throws JsonProcessingException {
    paymentRequest = requestFixture.bicCode("AGBLLT2X").build();
    String serialized = mapper.writeValueAsString(paymentRequest);
    assertThat(serialized, equalTo(paymentJson));
  }

  @Test
  void shouldDeserializePaymentRequest() throws JsonProcessingException {
    paymentRequest = requestFixture.bicCode("AGBLLT2X").build();
    PaymentRequest deserialized = mapper.readValue(paymentJson, PaymentRequest.class);
    assertThat(deserialized, equalTo(paymentRequest));
  }

  @Test
  void shouldValidatePaymentRequestSuccessfully() {
    paymentRequest = requestFixture.build();
    assertThat(paymentRequest.valid(), isOptionalOf(null));
  }

  @Test
  void shouldFailValidatingIfTypeIsNull() {
    paymentRequest = requestFixture.type(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'type' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfTypeIsInvalid() {
    paymentRequest = requestFixture.type("EUR").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'type' value 'EUR' is not valid"));
  }

  @Test
  void shouldFailValidatingIfAmountIsNull() {
    paymentRequest = requestFixture.amount(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'amount' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfAmountIsInvalid() {
    paymentRequest = requestFixture.amount(0).build();
    String expected = "'amount' must be greater than or equal to 0.01";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingIfCurrencyIsNull() {
    paymentRequest = requestFixture.currency(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'currency' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfCurrencyIsInvalid() {
    paymentRequest = requestFixture.currency("LTL").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'currency' value 'LTL' is not valid"));
  }

  @Test
  void shouldFailValidatingIfDebtorIbanIsNull() {
    paymentRequest = requestFixture.debtorIban(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'debtor_iban' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfDebtorIbanIsTooLong() {
    paymentRequest = requestFixture.debtorIban("LT3456789012345678901").build();
    String expected = "'debtor_iban' length must be less than or equal to 20";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingIfCreditorIbanIsNull() {
    paymentRequest = requestFixture.creditorIban(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'creditor_iban' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfCreditorIbanIsTooLong() {
    paymentRequest = requestFixture.creditorIban("LT3456789012345678901").build();
    String expected = "'creditor_iban' length must be less than or equal to 20";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingIfBicCodeIsTooLong() {
    paymentRequest = requestFixture.bicCode("LT3456789012345678901").build();
    String expected = "'bic_code' length must be less than or equal to 20";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingIfDetailsIsTooLong() {
    paymentRequest = requestFixture.details("details details details details "
        + "details details details details details details details details details details "
        + "details details details details details details details details details details "
        + "details details details details details details details details ").build();
    String expected = "'details' length must be less than or equal to 255";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingType1PaymentIfDetailsIsNull() {
    paymentRequest = requestFixture.type("TYPE1").details(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'details' cannot be null for TYPE1 payment"));
  }

  @Test
  void shouldFailValidatingType1PaymentIfCurencyIsNotEUR() {
    paymentRequest = requestFixture.type("TYPE1").currency("USD").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'currency' must be 'EUR' for TYPE1 payment"));
  }

  @Test
  void shouldFailValidatingType1PaymentIfBicCodeIsNotNull() {
    paymentRequest = requestFixture.type("TYPE1").bicCode("BIC").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'bic_code' not available for TYPE1 payment"));
  }

  @Test
  void shouldValidateType2PaymentRequestSuccessfully() {
    paymentRequest = requestFixture.type("TYPE2").currency("USD").build();
    assertThat(paymentRequest.valid(), isOptionalOf(null));
  }

  @Test
  void shouldFailValidatingType2PaymentIfCurencyIsNotUSD() {
    paymentRequest = requestFixture.type("TYPE2").currency("EUR").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'currency' must be 'USD' for TYPE2 payment"));
  }

  @Test
  void shouldFailValidatingType2PaymentIfBicCodeIsNotNull() {
    paymentRequest = requestFixture.type("TYPE2").currency("USD").bicCode("BIC").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'bic_code' not available for TYPE2 payment"));
  }

  @Test
  void shouldValidateType3PaymentRequestSuccessfully() {
    paymentRequest = requestFixture.type("TYPE3").bicCode("BIC").details(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf(null));
  }

  @Test
  void shouldFailValidatingType3PaymentIfBicCodeIsNull() {
    paymentRequest = requestFixture.type("TYPE3").details(null).bicCode(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'bic_code' cannot be null for TYPE3 payment"));
  }

  @Test
  void shouldFailValidatingType3PaymentIfDetailsIsNotNull() {
    paymentRequest = requestFixture.type("TYPE3").bicCode("BIC").details("details").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'details' not available for TYPE3 payment"));
  }
}
