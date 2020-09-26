package com.gmail.tikrai.payments.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.tikrai.payments.util.Generated;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class Payment {
  private final int id;
  private final String type;
  private final BigDecimal amount;
  private final String currency;
  private final String debtorIban;
  private final String creditorIban;
  private final String bicCode;
  private final String details;

  @JsonCreator
  public Payment(
      int id,
      String type,
      BigDecimal amount,
      String currency,
      String debtorIban,
      String creditorIban,
      String bicCode,
      String details
  ) {
    this.id = id;
    this.type = type;
    this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    this.currency = currency;
    this.debtorIban = debtorIban;
    this.creditorIban = creditorIban;
    this.bicCode = bicCode;
    this.details = details;
  }

  public Payment withId(int id) {
    return new Payment(id, type, amount, currency, debtorIban, creditorIban, bicCode, details);
  }

  @JsonProperty("id")
  public int id() {
    return id;
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
  public Optional<String> bicCode() {
    return Optional.ofNullable(bicCode);
  }

  @JsonProperty("details")
  public Optional<String> details() {
    return Optional.ofNullable(details);
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
    Payment payment = (Payment) o;
    return id == payment.id
        && Objects.equals(type, payment.type)
        && Objects.equals(amount, payment.amount)
        && Objects.equals(currency, payment.currency)
        && Objects.equals(debtorIban, payment.debtorIban)
        && Objects.equals(creditorIban, payment.creditorIban)
        && Objects.equals(bicCode, payment.bicCode)
        && Objects.equals(details, payment.details);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(id, type, amount, currency, debtorIban, creditorIban, bicCode, details);
  }
}
