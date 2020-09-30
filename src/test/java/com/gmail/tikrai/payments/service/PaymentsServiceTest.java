package com.gmail.tikrai.payments.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.domain.Payment.Type;
import com.gmail.tikrai.payments.exception.ConflictException;
import com.gmail.tikrai.payments.exception.PaymentNotFoundException;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.PaymentCancelFeeResponse;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class PaymentsServiceTest {
  private final PaymentsRepository paymentsRepository = mock(PaymentsRepository.class);
  private final IpResolveService ipResolveService = mock(IpResolveService.class);
  private final TimeService timeService = mock(TimeService.class);
  private final String timezone = "Europe/Vilnius";
  private final PaymentsService paymentsService =
      new PaymentsService(paymentsRepository, ipResolveService, timeService, timezone);

  private final Payment payment = Fixture.payment().build();
  private final Instant now = Instant.now();
  private final Instant creatTime = Instant.parse("2020-09-30T18:33:47.053Z");


  @Test
  void shouldFindAllNonCancelledPayments() {
    List<Payment> paymentList = Collections.singletonList(payment);
    when(paymentsRepository.findAllPending(null, null)).thenReturn(paymentList);

    List<Integer> actual = paymentsService.findAllPending(null, null);

    List<Integer> expected = Collections.singletonList(payment.id());
    assertThat(actual, equalTo(expected));
    verify(paymentsRepository).findAllPending(null, null);
  }

  @Test
  void shouldGetCancellingFeeByIdForTwoHourOldPayment() {
    int hours = 2;
    Instant requestTime = creatTime.plus(Duration.ofHours(hours));
    Payment payment = Fixture.payment().type(Type.TYPE1).created(creatTime).build();
    when(timeService.now()).thenReturn(requestTime);
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.of(payment));

    PaymentCancelFeeResponse actualResponse = paymentsService.getCancellingFee(payment.id());

    BigDecimal expactedPrice = BigDecimal.valueOf(payment.type().cancelCoeff * hours, 2);
    PaymentCancelFeeResponse expectedResponse =
        new PaymentCancelFeeResponse(0, true, expactedPrice);
    assertThat(actualResponse, equalTo(expectedResponse));
    verify(paymentsRepository).findById(payment.id());
    verify(timeService).now();
  }

  @Test
  void shouldGetCancellingFeeByIdForAlmostHourOldPayment() {
    Instant requestTime = creatTime.plus(Duration.ofHours(1).minus(Duration.ofSeconds(1)));
    Payment payment = Fixture.payment().created(creatTime).build();
    when(timeService.now()).thenReturn(requestTime);
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.of(payment));

    PaymentCancelFeeResponse actualResponse = paymentsService.getCancellingFee(payment.id());

    BigDecimal expectedPrice = BigDecimal.valueOf(0, 2);
    PaymentCancelFeeResponse expectedResponse =
        new PaymentCancelFeeResponse(0, true, expectedPrice);
    assertThat(actualResponse, equalTo(expectedResponse));
    verify(paymentsRepository).findById(payment.id());
    verify(timeService).now();
  }

  @Test
  void shouldGetCancellingNotPossibleById() {
    when(timeService.now()).thenReturn(now);
    Payment oldPayment = payment.withCreated(now.minus(Duration.ofHours(25)));
    when(paymentsRepository.findById(oldPayment.id())).thenReturn(Optional.of(oldPayment));

    PaymentCancelFeeResponse actual = paymentsService.getCancellingFee(oldPayment.id());

    PaymentCancelFeeResponse expected = new PaymentCancelFeeResponse(0, false, null);
    assertThat(actual, equalTo(expected));
    verify(paymentsRepository).findById(payment.id());
    verify(timeService).now();
  }

  @Test
  void shouldFailToGetCancellingFeeByIdIfNotExists() {
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.empty());

    String message = assertThrows(
        PaymentNotFoundException.class,
        () -> paymentsService.getCancellingFee(payment.id())
    ).getMessage();

    String expectedMessage = String.format("Payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findById(payment.id());
  }

  @Test
  void shouldCreateNewPayment() {
    when(timeService.now()).thenReturn(now);
    Payment paymentNow = payment.withCreated(now);
    when(paymentsRepository.create(paymentNow)).thenReturn(paymentNow);

    Payment actual = paymentsService.create(payment);

    assertThat(actual, equalTo(paymentNow));
    verify(paymentsRepository).create(paymentNow);
    verify(ipResolveService).resolveIpAdress(paymentNow);
    verify(timeService).now();
  }

  @Test
  void shouldCancelPayment() {
    Payment cancelled = payment.withCancelled(true);
    BigDecimal zero = BigDecimal.valueOf(0, 2);
    when(paymentsRepository.findById(payment.id()))
        .thenReturn(Optional.of(payment.withCancelFee(zero)));
    when(timeService.now()).thenReturn(now);
    when(paymentsRepository.cancel(payment.id(), zero)).thenReturn(Optional.of(cancelled));

    Payment actual = paymentsService.cancel(payment.id());

    assertThat(actual, equalTo(cancelled));
    verify(paymentsRepository).findById(payment.id());
    verify(timeService).now();
    verify(paymentsRepository).cancel(payment.id(), zero);
  }

  @Test
  void shouldFailToCancelPaymentIfNotExists() {
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.empty());

    String message = assertThrows(
        PaymentNotFoundException.class,
        () -> paymentsService.cancel(payment.id())
    ).getMessage();

    String expectedMessage = String.format("Payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findById(payment.id());
  }

  @Test
  void shouldFailToCancelPaymentIfAfterCalculationDoNotExistAnymore() {
    BigDecimal zero = BigDecimal.valueOf(0, 2);
    when(paymentsRepository.findById(payment.id()))
        .thenReturn(Optional.of(payment));
    when(timeService.now()).thenReturn(now);
    when(paymentsRepository.cancel(payment.id(), zero))
        .thenReturn(Optional.empty());

    String message = assertThrows(
        PaymentNotFoundException.class,
        () -> paymentsService.cancel(payment.id())
    ).getMessage();

    String expectedMessage = String.format("Payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findById(payment.id());
    verify(timeService).now();
    verify(paymentsRepository).cancel(payment.id(), zero);
  }

  @Test
  void shouldFailToCancelPaymentIfPeriodExpired() {
    when(timeService.now()).thenReturn(now);
    Payment expired = payment.withCreated(now.minus(Duration.ofHours(25)));
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.of(expired));

    String message = assertThrows(
        ConflictException.class,
        () -> paymentsService.cancel(payment.id())
    ).getMessage();

    String expectedMessage = String.format("Not possible to cancel payment '%s'", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findById(payment.id());
    verify(timeService).now();
  }

  @AfterEach
  void verifyMocks() {
    verifyNoMoreInteractions(paymentsRepository, ipResolveService, timeService);
  }
}
