package com.gmail.tikrai.payments.fixture;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.domain.Payment.Currency;
import com.gmail.tikrai.payments.domain.Payment.Type;
import java.math.BigDecimal;
import java.time.Instant;

public class PaymentFixture {

  private int id = 0;
  private Instant created = Instant.now();
  private boolean cancelled = false;
  private BigDecimal cancelFee = null;
  private Type type = Type.TYPE1;
  private BigDecimal amount = BigDecimal.valueOf(10.01);
  private Currency currency = Currency.EUR;
  private String debtorIban = "LT0001";
  private String creditorIban = "LT9999";
  private String bicCode = "AGBLLT2X";
  private String details = "details";
  private String ipAddress = null;
  private String country = null;

  public PaymentFixture id(int id) {
    this.id = id;
    return this;
  }

  public PaymentFixture created(Instant created) {
    this.created = created;
    return this;
  }

  public PaymentFixture cancelled(boolean cancelled) {
    this.cancelled = cancelled;
    return this;
  }

  public PaymentFixture cancelFee(BigDecimal cancelFee) {
    this.cancelFee = cancelFee;
    return this;
  }

  public PaymentFixture type(Type type) {
    this.type = type;
    return this;
  }

  public PaymentFixture amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public PaymentFixture amount(double amount) {
    this.amount = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    return this;
  }

  public PaymentFixture currency(Currency currency) {
    this.currency = currency;
    return this;
  }

  public PaymentFixture debtorIban(String debtorIban) {
    this.debtorIban = debtorIban;
    return this;
  }

  public PaymentFixture creditorIban(String creditorIban) {
    this.creditorIban = creditorIban;
    return this;
  }

  public PaymentFixture bicCode(String bicCode) {
    this.bicCode = bicCode;
    return this;
  }

  public PaymentFixture details(String details) {
    this.details = details;
    return this;
  }

  public PaymentFixture ipAddress(String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }

  public PaymentFixture country(String country) {
    this.country = country;
    return this;
  }

  public Payment build() {
    return new Payment(id, created, cancelled, cancelFee, type, amount, currency, debtorIban,
        creditorIban, bicCode, details, ipAddress, country);
  }
}
