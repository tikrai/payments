package com.gmail.tikrai.payments.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.tikrai.payments.util.Generated;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class Payment {

  public enum Type {
    TYPE1,
    TYPE2,
    TYPE3,
  }

  public enum Currency {
    EUR, USD,
  }

  private final int id;
  private final Instant created;
  private final Instant cancelled;
  private final BigDecimal cancelFee;
  private final Type type;
  private final BigDecimal amount;
  private final Currency currency;
  private final String debtorIban;
  private final String creditorIban;
  private final String bicCode;
  private final String details;
  private final Integer cancelCoeff;
  private final String ipAddress;
  private final Boolean notified;

  @JsonCreator
  public Payment(
      int id,
      Instant created,
      Instant cancelled,
      BigDecimal cancelFee,
      Type type,
      BigDecimal amount,
      Currency currency,
      String debtorIban,
      String creditorIban,
      String bicCode,
      String details,
      Integer cancelCoeff,
      String ipAddress,
      Boolean notified
  ) {
    this.id = id;
    this.created = created;
    this.cancelled = cancelled;
    this.cancelFee = cancelFee;
    this.type = type;
    this.amount = amount.setScale(2, BigDecimal.ROUND_UNNECESSARY);
    this.currency = currency;
    this.debtorIban = debtorIban;
    this.creditorIban = creditorIban;
    this.bicCode = bicCode;
    this.details = details;
    this.cancelCoeff = cancelCoeff;
    this.ipAddress = ipAddress;
    this.notified = notified;
  }

  public Payment withId(int id) {
    return new Payment(id, created, cancelled, cancelFee, type, amount, currency, debtorIban,
        creditorIban, bicCode, details, cancelCoeff, ipAddress, notified);
  }

  public Payment withCreated(Instant created) {
    return new Payment(id, created, cancelled, cancelFee, type, amount, currency, debtorIban,
        creditorIban, bicCode, details, cancelCoeff, ipAddress, notified);
  }

  @JsonProperty("id")
  public int id() {
    return id;
  }

  @JsonProperty("created")
  public Instant created() {
    return created;
  }

  @JsonProperty("cancelled")
  public Optional<Instant> cancelled() {
    return Optional.ofNullable(cancelled);
  }

  @JsonProperty("cancel_fee")
  public BigDecimal cancelFee() {
    return cancelFee;
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

  @JsonProperty("cancel_coeff")
  public Optional<Integer> cancelCoeff() {
    return Optional.ofNullable(cancelCoeff);
  }

  @JsonProperty("ip_address")
  public Optional<String> ipAddress() {
    return Optional.ofNullable(ipAddress);
  }

  @JsonProperty("notified")
  public Optional<Boolean> notified() {
    return Optional.ofNullable(notified);
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
        && Objects.equals(created, payment.created)
        && Objects.equals(cancelled, payment.cancelled)
        && Objects.equals(cancelFee, payment.cancelFee)
        && type == payment.type
        && Objects.equals(amount, payment.amount)
        && currency == payment.currency
        && Objects.equals(debtorIban, payment.debtorIban)
        && Objects.equals(creditorIban, payment.creditorIban)
        && Objects.equals(bicCode, payment.bicCode)
        && Objects.equals(details, payment.details)
        && Objects.equals(cancelCoeff, payment.cancelCoeff)
        && Objects.equals(ipAddress, payment.ipAddress)
        && Objects.equals(notified, payment.notified);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects
        .hash(id, created, cancelled, cancelFee, type, amount, currency, debtorIban, creditorIban,
            bicCode, details, cancelCoeff, ipAddress, notified);
  }

  @Override
  @Generated
  public String toString() {
    return "Payment{" +
        "id=" + id +
        ", created=" + created +
        ", cancelled=" + cancelled +
        ", cancelFee=" + cancelFee +
        ", type=" + type +
        ", amount=" + amount +
        ", currency=" + currency +
        ", debtorIban='" + debtorIban + '\'' +
        ", creditorIban='" + creditorIban + '\'' +
        ", bicCode='" + bicCode + '\'' +
        ", details='" + details + '\'' +
        ", cancelCoeff=" + cancelCoeff +
        ", ipAddress='" + ipAddress + '\'' +
        ", notified=" + notified +
        '}';
  }
}
