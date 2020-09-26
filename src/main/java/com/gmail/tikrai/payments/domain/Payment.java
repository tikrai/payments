package com.gmail.tikrai.payments.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.tikrai.payments.util.Generated;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class Payment {

  public enum Type {
    TYPE1, TYPE2, TYPE3,
  }

  public enum Currency {
    EUR, USD,
  }

  private final int id;
  private final boolean cancelled;
  private final Type type;
  private final BigDecimal amount;
  private final Currency currency;
  private final String debtorIban;
  private final String creditorIban;
  private final String bicCode;
  private final String details;
  private final String ipAddress;
  private final String country;

  @JsonCreator
  public Payment(
      int id,
      boolean cancelled,
      Type type,
      BigDecimal amount,
      Currency currency,
      String debtorIban,
      String creditorIban,
      String bicCode,
      String details,
      String ipAddress,
      String country
  ) {
    this.id = id;
    this.cancelled = cancelled;
    this.type = type;
    this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    this.currency = currency;
    this.debtorIban = debtorIban;
    this.creditorIban = creditorIban;
    this.bicCode = bicCode;
    this.details = details;
    this.ipAddress = ipAddress;
    this.country = country;
  }

  public Payment withId(int id) {
    return new Payment(id, cancelled, type, amount, currency, debtorIban, creditorIban,
        bicCode, details, ipAddress, country);
  }

  public Payment withCancelled(boolean cancelled) {
    return new Payment(id, cancelled, type, amount, currency, debtorIban, creditorIban,
        bicCode, details, ipAddress, country);
  }

  @JsonProperty("id")
  public int id() {
    return id;
  }

  @JsonProperty("cancelled")
  public boolean cancelled() {
    return cancelled;
  }

  @JsonProperty("type")
  public Type type() {
    return type;
  }

  @JsonProperty("amount")
  public BigDecimal amount() {
    return amount;
  }

  @JsonProperty("currency")
  public Currency currency() {
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

  @JsonProperty("ip_address")
  public Optional<String> ipAddress() {
    return Optional.ofNullable(ipAddress);
  }

  @JsonProperty("country")
  public Optional<String> country() {
    return Optional.ofNullable(country);
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
        && cancelled == payment.cancelled
        && type == payment.type
        && Objects.equals(amount, payment.amount)
        && currency == payment.currency
        && Objects.equals(debtorIban, payment.debtorIban)
        && Objects.equals(creditorIban, payment.creditorIban)
        && Objects.equals(bicCode, payment.bicCode)
        && Objects.equals(details, payment.details)
        && Objects.equals(ipAddress, payment.ipAddress)
        && Objects.equals(country, payment.country);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects
        .hash(id, cancelled, type, amount, currency, debtorIban, creditorIban, bicCode, details,
            ipAddress, country);
  }

  @Override
  @Generated
  public String toString() {
    return "Payment{" +
        "id=" + id +
        ", cancelled=" + cancelled +
        ", type=" + type +
        ", amount=" + amount +
        ", currency=" + currency +
        ", debtorIban='" + debtorIban + '\'' +
        ", creditorIban='" + creditorIban + '\'' +
        ", bicCode='" + bicCode + '\'' +
        ", details='" + details + '\'' +
        ", ipAddress='" + ipAddress + '\'' +
        ", country='" + country + '\'' +
        '}';
  }
}
