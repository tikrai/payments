package com.gmail.tikrai.payments.fixture;

import com.gmail.tikrai.payments.domain.Payment.Currency;
import com.gmail.tikrai.payments.domain.Payment.Type;
import com.gmail.tikrai.payments.domain.PaymentRequest;
import java.math.BigDecimal;

public class PaymentRequestFixture {

  private String type = Type.TYPE1.toString();
  private BigDecimal amount = BigDecimal.valueOf(10.01);
  private String currency = Currency.EUR.toString();
  private String debtorIban = "LT0001";
  private String creditorIban = "LT9999";
  private String bicCode = "AGBLLT2X";
  private String details = null;

  public PaymentRequestFixture type(String type) {
    this.type = type;
    return this;
  }

  public PaymentRequestFixture amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public PaymentRequestFixture currency(String currency) {
    this.currency = currency;
    return this;
  }

  public PaymentRequestFixture debtorIban(String debtorIban) {
    this.debtorIban = debtorIban;
    return this;
  }

  public PaymentRequestFixture creditorIban(String creditorIban) {
    this.creditorIban = creditorIban;
    return this;
  }

  public PaymentRequestFixture bicCode(String bicCode) {
    this.bicCode = bicCode;
    return this;
  }

  public PaymentRequestFixture details(String details) {
    this.details = details;
    return this;
  }

  public PaymentRequest build() {
    return new PaymentRequest(type, amount, currency, debtorIban, creditorIban, bicCode, details);
  }
}
