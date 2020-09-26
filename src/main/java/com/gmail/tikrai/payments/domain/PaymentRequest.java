package com.gmail.tikrai.payments.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gmail.tikrai.payments.util.Generated;
import java.math.BigDecimal;
import java.util.Objects;

public class PaymentRequest {
  private final String type;
  private final BigDecimal amount;
  private final String currency;
  private final String debtorIban;
  private final String creditorIban;
  private final String bicCode;
  private final String details;

  @JsonCreator
  public PaymentRequest(
      String type,
      BigDecimal amount,
      String currency,
      String debtorIban,
      String creditorIban,
      String bicCode,
      String details
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
}
