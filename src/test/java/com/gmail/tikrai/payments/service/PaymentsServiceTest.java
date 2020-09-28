package com.gmail.tikrai.payments.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ConflictException;
import com.gmail.tikrai.payments.exception.ResourceNotFoundException;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.PaymentCancelFeeResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PaymentsServiceTest {
  private final PaymentsRepository paymentsRepository = mock(PaymentsRepository.class);
  private final String timezone = "Europe/Vilnius";
  private final PaymentsService paymentsService = new PaymentsService(paymentsRepository, timezone);

  private final Payment payment = Fixture.payment().build();
  private final List<Payment> paymentList = Collections.singletonList(payment);


  @Test
  void shouldFindAllNonCancelledPayments() {
    when(paymentsRepository.findAllPending(null, null)).thenReturn(paymentList);

    List<Payment> actual = paymentsService.findAllPending(null, null);

    assertThat(actual, equalTo(paymentList));
    verify(paymentsRepository).findAllPending(null, null);
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldGetCancellingFeeById() {
    Payment secOldPayment = payment.withCreated(payment.created().minusSeconds(1));
    when(paymentsRepository.findById(secOldPayment.id())).thenReturn(Optional.of(secOldPayment));

    PaymentCancelFeeResponse actual = paymentsService.getCancellingFee(secOldPayment.id());

    PaymentCancelFeeResponse expected =
        new PaymentCancelFeeResponse(0, true, BigDecimal.valueOf(0, 2));
    assertThat(actual, equalTo(expected));
    verify(paymentsRepository).findById(secOldPayment.id());
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldGetCancellingNotPossibleById() {
    Payment oldPayment = payment.withCreated(payment.created().minusSeconds(3600 * 25));
    when(paymentsRepository.findById(oldPayment.id())).thenReturn(Optional.of(oldPayment));

    PaymentCancelFeeResponse actual = paymentsService.getCancellingFee(oldPayment.id());

    PaymentCancelFeeResponse expected = new PaymentCancelFeeResponse(0, false, null);
    assertThat(actual, equalTo(expected));
    verify(paymentsRepository).findById(payment.id());
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldFailToGetCancellingFeeById() {
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.empty());

    String message = assertThrows(
        ResourceNotFoundException.class,
        () -> paymentsService.getCancellingFee(payment.id())
    ).getMessage();

    String expectedMessage = String.format("Payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findById(payment.id());
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldCreateNewPayment() {
    when(paymentsRepository.create(payment)).thenReturn(payment);

    Payment actual = paymentsService.create(payment);

    assertThat(actual, equalTo(payment));
    verify(paymentsRepository).create(payment);
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldCancelPayment() {
    Payment cancelled = payment.withCancelled(true);
    BigDecimal zero = BigDecimal.valueOf(0, 2);
    when(paymentsRepository.findById(payment.id()))
        .thenReturn(Optional.of(payment.withCancelFee(zero)));
    when(paymentsRepository.cancel(payment.id(), zero)).thenReturn(cancelled);

    Payment actual = paymentsService.cancel(payment.id());

    assertThat(actual, equalTo(cancelled));
    verify(paymentsRepository).cancel(payment.id(), zero);
    verify(paymentsRepository).findById(payment.id());
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldFailToCancelPaymentIfNotExists() {
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.empty());

    String message = assertThrows(
        ResourceNotFoundException.class,
        () -> paymentsService.cancel(payment.id())
    ).getMessage();

    String expectedMessage = String.format("Payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findById(payment.id());
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldFailToCancelPaymentIfPeriodExpired() {
    Payment expired = payment.withCreated(payment.created().minusSeconds(3600 * 25));
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.of(expired));

    String message = assertThrows(
        ConflictException.class,
        () -> paymentsService.cancel(payment.id())
    ).getMessage();


    String expectedMessage = String.format("Not possible to cancel payment '%s'", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findById(payment.id());
    verifyNoMoreInteractions(paymentsRepository);
  }
}
