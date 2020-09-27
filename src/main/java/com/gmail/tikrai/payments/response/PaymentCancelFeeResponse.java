package com.gmail.tikrai.payments.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.tikrai.payments.util.Generated;
import java.math.BigDecimal;
import java.util.Objects;

public class PaymentCancelFeeResponse {
  private final int id;
  private final boolean cancelPossible;
  private final BigDecimal price;

  @JsonCreator
  public PaymentCancelFeeResponse(
      @JsonProperty("id") int id,
      @JsonProperty("cancel_possible") boolean cancelPossible,
      @JsonProperty("price") BigDecimal price
  ) {
    this.id = id;
    this.cancelPossible = cancelPossible;
    this.price = price;
  }

  @JsonProperty("id")
  public int id() {
    return id;
  }

  @JsonProperty("cancel_possible")
  public boolean cancelPossible() {
    return cancelPossible;
  }

  @JsonProperty("price")
  public BigDecimal price() {
    return price;
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
    PaymentCancelFeeResponse response = (PaymentCancelFeeResponse) o;
    return id == response.id
        && cancelPossible == response.cancelPossible
        && Objects.equals(price, response.price);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(id, cancelPossible, price);
  }

  @Override
  @Generated
  public String toString() {
    return "PaymentCancelFeeResponse{" +
        "id=" + id +
        ", cancelPossible=" + cancelPossible +
        ", price=" + price +
        '}';
  }
}
