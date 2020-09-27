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
      + "\"type\":\"TYPE1\",\"amount\":10.01,\"currency\":\"EUR\","
      + "\"debtor_iban\":\"LT0001\",\"creditor_iban\":\"LT9999\",\"bic_code\":\"AGBLLT2X\""
      + "}";
  private final PaymentRequestFixture requestFixture = Fixture.paymentRequest();
  private final PaymentRequest paymentRequest = requestFixture.build();

  @Test
  void shouldSerializePaymentRequest() throws JsonProcessingException {
    String serialized = mapper.writeValueAsString(paymentRequest);
    assertThat(serialized, equalTo(paymentJson));
  }

  @Test
  void shouldDeserializePaymentRequest() throws JsonProcessingException {
    PaymentRequest deserialized = mapper.readValue(paymentJson, PaymentRequest.class);
    assertThat(deserialized, equalTo(paymentRequest));
  }

  @Test
  void shouldValidatePaymentRequestSuccessfully() {
    assertThat(paymentRequest.valid(), isOptionalOf(null));
  }

  @Test
  void shouldFailValidatingIfTypeIsNull() {
    PaymentRequest paymentRequest = requestFixture.type(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'type' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfTypeIsInvalid() {
    PaymentRequest paymentRequest = requestFixture.type("EUR").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'type' value 'EUR' is not valid"));
  }

  @Test
  void shouldFailValidatingIfAmountIsNull() {
    PaymentRequest paymentRequest = requestFixture.amount(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'amount' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfAmountIsInvalid() {
    PaymentRequest paymentRequest = requestFixture.amount(0).build();
    String expected = "'amount' must be greater than or equal to 0.01";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingIfCurrencyIsNull() {
    PaymentRequest paymentRequest = requestFixture.currency(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'currency' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfCurrencyIsInvalid() {
    PaymentRequest paymentRequest = requestFixture.currency("LTL").build();
    assertThat(paymentRequest.valid(), isOptionalOf("'currency' value 'LTL' is not valid"));
  }

  @Test
  void shouldFailValidatingIfDebtorIbanIsNull() {
    PaymentRequest paymentRequest = requestFixture.debtorIban(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'debtor_iban' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfDebtorIbanIsTooLong() {
    PaymentRequest paymentRequest = requestFixture.debtorIban("LT3456789012345678901").build();
    String expected = "'debtor_iban' length must be less than or equal to 20";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingIfCreditorIbanIsNull() {
    PaymentRequest paymentRequest = requestFixture.creditorIban(null).build();
    assertThat(paymentRequest.valid(), isOptionalOf("'creditor_iban' cannot be null"));
  }

  @Test
  void shouldFailValidatingIfCreditorIbanIsTooLong() {
    PaymentRequest paymentRequest = requestFixture.creditorIban("LT3456789012345678901").build();
    String expected = "'creditor_iban' length must be less than or equal to 20";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingIfBicCodeIsTooLong() {
    PaymentRequest paymentRequest = requestFixture.bicCode("LT3456789012345678901").build();
    String expected = "'bic_code' length must be less than or equal to 20";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }

  @Test
  void shouldFailValidatingIfDetailsIsTooLong() {
    PaymentRequest paymentRequest = requestFixture.details("details details details details "
        + "details details details details details details details details details details "
        + "details details details details details details details details details details "
        + "details details details details details details details details ").build();
    String expected = "'details' length must be less than or equal to 255";
    assertThat(paymentRequest.valid(), isOptionalOf(expected));
  }


}
