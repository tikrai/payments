package com.gmail.tikrai.payments.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.util.Generated;
import com.gmail.tikrai.payments.validation.Validator;
import com.gmail.tikrai.payments.validation.ValidatorGroup;
import com.gmail.tikrai.payments.validation.validators.NullValidator;
import com.gmail.tikrai.payments.validation.validators.PaymentValidator;
import com.gmail.tikrai.payments.validation.validators.RegexValidator;
import com.gmail.tikrai.payments.validation.validators.SizeValidator;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class PaymentRequest implements Validator {
  private final String type;
  private final BigDecimal amount;
  private final String currency;
  private final String debtorIban;
  private final String creditorIban;
  private final String bicCode;
  private final String details;

  @JsonCreator
  public PaymentRequest(
      @JsonProperty("type") String type,
      @JsonProperty("amount") BigDecimal amount,
      @JsonProperty("currency") String currency,
      @JsonProperty("debtor_iban") String debtorIban,
      @JsonProperty("creditor_iban") String creditorIban,
      @JsonProperty("bic_code") String bicCode,
      @JsonProperty("details") String details
  ) {
    this.type = type;
    this.amount = amount;
    this.currency = currency;
    this.debtorIban = debtorIban;
    this.creditorIban = creditorIban;
    this.bicCode = bicCode;
    this.details = details;
  }

  public Payment toDomain() {
    return new Payment(
        0,
        false,
        Payment.Type.valueOf(type),
        amount,
        Payment.Currency.valueOf(currency),
        debtorIban,
        creditorIban,
        bicCode,
        details,
        null,
        null
    );
  }

  @JsonProperty("type")
  public String type() {
    return type;
  }

  @JsonProperty("amount")
  public BigDecimal amount() {
    return amount;
  }

  @JsonProperty("currency")
  public String currency() {
    return currency;
  }

  @JsonProperty("debtor_iban")
  public String debtorIban() {
    return debtorIban;
  }

  @JsonProperty("creditor_iban")
  public String creditorIban() {
    return creditorIban;
  }

  @JsonProperty("bic_code")
  public String bicCode() {
    return bicCode;
  }

  @JsonProperty("details")
  public String details() {
    return details;
  }

  @Override
  public Optional<String> valid() {
    return ValidatorGroup.of(
        ValidatorGroup.of(
            () -> NullValidator.not("type", type).valid(),
            () -> RegexValidator.of("type", type, "TYPE[123]").valid()
        ),
        ValidatorGroup.of(
            () -> NullValidator.not("amount", amount).valid(),
            () -> SizeValidator.min("amount", amount, BigDecimal.valueOf(0.01)).valid()
        ),
        ValidatorGroup.of(
            () -> NullValidator.not("currency", currency).valid(),
            () -> RegexValidator.of("currency", currency, "EUR|USD").valid()
        ),
        ValidatorGroup.of(
            () -> NullValidator.not("debtor_iban", debtorIban).valid(),
            () -> SizeValidator.max("debtor_iban", debtorIban, 20).valid()
        ),
        ValidatorGroup.of(
            () -> NullValidator.not("creditor_iban", creditorIban).valid(),
            () -> SizeValidator.max("creditor_iban", creditorIban, 20).valid()
        ),
        () -> SizeValidator.max("bic_code", bicCode, 20).valid(),
        () -> SizeValidator.max("details", details, 255).valid(),
        () -> PaymentValidator.ofType1(this).valid(),
        () -> PaymentValidator.ofType2(this).valid(),
        () -> PaymentValidator.ofType3(this).valid()
    ).valid();
  }

  @Override
  @Generated
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentRequest that = (PaymentRequest) o;
    return Objects.equals(type, that.type)
        && Objects.equals(amount, that.amount)
        && Objects.equals(currency, that.currency)
        && Objects.equals(debtorIban, that.debtorIban)
        && Objects.equals(creditorIban, that.creditorIban)
        && Objects.equals(bicCode, that.bicCode)
        && Objects.equals(details, that.details);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(type, amount, currency, debtorIban, creditorIban, bicCode, details);
  }

  @Override
  @Generated
  public String toString() {
    return "PaymentRequest{" +
        "type='" + type + '\'' +
        ", amount=" + amount +
        ", currency='" + currency + '\'' +
        ", debtorIban='" + debtorIban + '\'' +
        ", creditorIban='" + creditorIban + '\'' +
        ", bicCode='" + bicCode + '\'' +
        ", details='" + details + '\'' +
        '}';
  }
}
