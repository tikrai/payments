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
  private String ipAddress = "127.0.0.1";
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

  public PaymentFixture cancelFee(double cancelFee) {
    this.cancelFee = BigDecimal.valueOf(cancelFee).setScale(2, BigDecimal.ROUND_UNNECESSARY);
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
    this.amount = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_UNNECESSARY);
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

  public PaymentFixture of(Payment payment) {
    this.id = payment.id();
    this.created = payment.created();
    this.cancelled = payment.cancelled();
    this.cancelFee = payment.cancelFee();
    this.type = payment.type();
    this.amount = payment.amount();
    this.currency = payment.currency();
    this.debtorIban = payment.debtorIban();
    this.creditorIban = payment.creditorIban();
    this.bicCode = payment.bicCode().orElse(null);
    this.details = payment.details().orElse(null);
    this.ipAddress = payment.ipAddress().orElse(null);
    this.country = payment.country().orElse(null);
    return this;
  }

  public Payment build() {
    return new Payment(id, created, cancelled, cancelFee, type, amount, currency, debtorIban,
        creditorIban, bicCode, details, ipAddress, country);
  }
}
