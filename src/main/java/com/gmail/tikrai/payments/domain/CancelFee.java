package com.gmail.tikrai.payments.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.tikrai.payments.util.Generated;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class CancelFee {
  private final int id;
  private final boolean cancelPossible;
  private final BigDecimal price;
  private final Instant time;

  @JsonCreator
  public CancelFee(
      @JsonProperty("id") int id,
      @JsonProperty("cancel_possible") boolean cancelPossible,
      @JsonProperty("price") BigDecimal price,
      @JsonProperty("time") Instant time
  ) {
    this.id = id;
    this.cancelPossible = cancelPossible;
    this.price = price;
    this.time = time;
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

  @JsonProperty("time")
  public Instant time() {
    return time;
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
    CancelFee that = (CancelFee) o;
    return id == that.id
        && cancelPossible == that.cancelPossible
        && Objects.equals(price, that.price)
        && Objects.equals(time, that.time);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(id, cancelPossible, price, time);
  }

  @Override
  @Generated
  public String toString() {
    return "CancelFee{" +
        "id=" + id +
        ", cancelPossible=" + cancelPossible +
        ", price=" + price +
        ", time=" + time +
        '}';
  }
}
