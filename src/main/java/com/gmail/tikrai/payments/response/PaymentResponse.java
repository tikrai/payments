package com.gmail.tikrai.payments.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.util.Generated;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@JsonPropertyOrder({"id", "created", "cancelled"})
public class PaymentResponse extends PaymentRequest {

  private final int id;
  private final Instant created;
  private final Instant cancelled;

  public PaymentResponse(
      int id,
      Instant created,
      Instant cancelled,
      String type,
      BigDecimal amount,
      String currency,
      String debtorIban,
      String creditorIban,
      String bicCode,
      String details
  ) {
    super(type, amount, currency, debtorIban, creditorIban, bicCode, details);
    this.id = id;
    this.created = created;
    this.cancelled = cancelled;
  }

  public static PaymentResponse of(Payment payment) {
    return new PaymentResponse(
        payment.id(),
        payment.created(),
        payment.cancelled().orElse(null),
        payment.type().toString(),
        payment.amount(),
        payment.currency().toString(),
        payment.debtorIban(),
        payment.creditorIban(),
        payment.bicCode().orElse(null),
        payment.details().orElse(null)
    );
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
  public Instant cancelled() {
    return cancelled;
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
    if (!super.equals(o)) {
      return false;
    }
    PaymentResponse that = (PaymentResponse) o;
    return id == that.id
        && Objects.equals(created, that.created)
        && Objects.equals(cancelled, that.cancelled);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, created, cancelled);
  }

  @Override
  @Generated
  public String toString() {
    return "PaymentResponse{" +
        "id=" + id +
        ", created=" + created +
        ", cancelled=" + cancelled +
        "} " + super.toString();
  }
}
