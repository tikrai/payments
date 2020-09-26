package com.gmail.tikrai.payments.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
