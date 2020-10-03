package com.gmail.tikrai.payments.service;

import com.gmail.tikrai.payments.domain.CancelFee;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ConflictException;
import com.gmail.tikrai.payments.exception.PaymentNotFoundException;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.IdResponse;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentsService {

  private final PaymentsRepository paymentsRepository;
  private final RestService restService;
  private final TimeService timeService;
  private final String timezone;

  @Autowired
  public PaymentsService(
      PaymentsRepository paymentsRepository,
      RestService restService,
      TimeService timeService,
      @Value("${payments.timezone}") String timezone
  ) {
    this.paymentsRepository = paymentsRepository;
    this.restService = restService;
    this.timeService = timeService;
    this.timezone = timezone;
  }

  private CancelFee cancelFee(Payment payment) {
    Instant now = timeService.now();
    LocalDate dateCreated = payment.created().atZone(ZoneId.of(timezone)).toLocalDate();
    LocalDate dateNow = now.atZone(ZoneId.of(timezone)).toLocalDate();

    if (!dateNow.equals(dateCreated)) {
      return new CancelFee(payment.id(), false, null, now);
    }

    long hours = Duration.between(payment.created(), now).toHours();
    long cents = hours * payment.cancelCoeff().get();
    BigDecimal euros = BigDecimal.valueOf(cents, 2);

    return new CancelFee(payment.id(), true, euros, now);
  }

  public List<IdResponse> findAllPending(BigDecimal min, BigDecimal max) {
    return paymentsRepository.findAllPending(min, max).stream()
        .map(Payment::id)
        .map(IdResponse::new)
        .collect(Collectors.toList());
  }

  public CancelFee getCancellingFee(int id) {
    Payment payment = paymentsRepository.findById(id)
        .orElseThrow(() -> new PaymentNotFoundException(id));
    return cancelFee(payment);
  }

  public Payment create(Payment payment) {
    Payment created = paymentsRepository.create(payment.withCreated(timeService.now()));
    restService.resolveIpAdress(created);
    restService.notifyPaymentSaved(created);
    return created;
  }

  public Payment cancel(int id) {
    Payment payment = paymentsRepository.findById(id)
        .orElseThrow(() -> new PaymentNotFoundException(id));
    CancelFee fee = cancelFee(payment);

    if (!fee.cancelPossible()) {
      throw new ConflictException(String.format("Not possible to cancel payment '%s'", id));
    }

    return paymentsRepository.cancel(fee).orElseThrow(() -> new PaymentNotFoundException(id));
  }
}
