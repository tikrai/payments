package com.gmail.tikrai.payments.service;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ConflictException;
import com.gmail.tikrai.payments.exception.PaymentNotFoundException;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.IdResponse;
import com.gmail.tikrai.payments.response.PaymentCancelFeeResponse;
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
  private final IpResolveService ipResolveService;
  private final TimeService timeService;
  private final String timezone;

  @Autowired
  public PaymentsService(
      PaymentsRepository paymentsRepository,
      IpResolveService ipResolveService,
      TimeService timeService,
      @Value("${payments.timezone}") String timezone
  ) {
    this.paymentsRepository = paymentsRepository;
    this.ipResolveService = ipResolveService;
    this.timeService = timeService;
    this.timezone = timezone;
  }

  private PaymentCancelFeeResponse cancelFee(Payment payment) {
    Instant now = timeService.now();
    LocalDate dateCreated = payment.created().atZone(ZoneId.of(timezone)).toLocalDate();
    LocalDate dateNow = now.atZone(ZoneId.of(timezone)).toLocalDate();

    if (!dateNow.equals(dateCreated)) {
      return new PaymentCancelFeeResponse(payment.id(), false, null);
    }

    long hours = Duration.between(payment.created(), now).toHours();
    long cents = hours * payment.type().cancelCoeff;
    BigDecimal euros = BigDecimal.valueOf(cents, 2);

    return new PaymentCancelFeeResponse(payment.id(), true, euros);
  }

  public List<IdResponse> findAllPending(BigDecimal min, BigDecimal max) {
    return paymentsRepository.findAllPending(min, max).stream()
        .map(Payment::id)
        .map(IdResponse::new)
        .collect(Collectors.toList());
  }

  public PaymentCancelFeeResponse getCancellingFee(int id) {
    Payment payment = paymentsRepository.findById(id)
        .orElseThrow(() -> new PaymentNotFoundException(id));
    return cancelFee(payment);
  }

  public Payment create(Payment payment) {
    Payment created = paymentsRepository.create(payment.withCreated(timeService.now()));
    ipResolveService.resolveIpAdress(created);
    return created;
  }

  public Payment cancel(int id) {
    PaymentNotFoundException notFound = new PaymentNotFoundException(id);

    Payment payment = paymentsRepository.findById(id).orElseThrow(() -> notFound);
    PaymentCancelFeeResponse fee = cancelFee(payment);

    if (!fee.cancelPossible()) {
      throw new ConflictException(String.format("Not possible to cancel payment '%s'", id));
    }

    return paymentsRepository.cancel(id, fee.price()).orElseThrow(() -> notFound);
  }
}
