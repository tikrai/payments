package com.gmail.tikrai.payments.service;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ConflictException;
import com.gmail.tikrai.payments.exception.ResourceNotFoundException;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.PaymentCancelFeeResponse;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentsService {

  private final PaymentsRepository paymentsRepository;
  private final String timezone;

  @Autowired
  public PaymentsService(
      PaymentsRepository paymentsRepository,
      @Value("${payments.timezone}") String timezone
  ) {
    this.paymentsRepository = paymentsRepository;
    this.timezone = timezone;
  }

  private PaymentCancelFeeResponse cancelFee(Payment payment) {
    LocalDate dateCreated = payment.created().atZone(ZoneId.of(timezone)).toLocalDate();
    LocalDate dateNow = Instant.now().atZone(ZoneId.of(timezone)).toLocalDate();

    if (!dateNow.equals(dateCreated)) {
      return new PaymentCancelFeeResponse(payment.id(), false, null);
    }

    long hours = Duration.between(payment.created(), Instant.now()).toHours();
    long cents = hours * payment.type().cancelCoeff;
    BigDecimal euros = BigDecimal.valueOf(cents, 2);

    return new PaymentCancelFeeResponse(payment.id(), true, euros);
  }

  public List<Payment> findAllPending() {
    return paymentsRepository.findAllPending();
  }

  public PaymentCancelFeeResponse getCancellingFee(int id) {
    Payment payment = paymentsRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Payment with id '%s' was not found", id)
        ));
    return cancelFee(payment);
  }

  public Payment create(Payment payment) {
    return paymentsRepository.create(payment);
  }

  public Payment cancel(int id) {
    Payment payment = paymentsRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Payment with id '%s' was not found", id)
        ));
    PaymentCancelFeeResponse fee = cancelFee(payment);

    if (!fee.cancelPossible()) {
      throw new ConflictException(String.format("Not possible to cancel payment '%s'", id));
    }

    return paymentsRepository.cancel(id, fee.price());
  }
}
