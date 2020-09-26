package com.gmail.tikrai.payments.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ResourceNotFoundException;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PaymentsServiceTest {
  private final PaymentsRepository paymentsRepository = mock(PaymentsRepository.class);
  private final PaymentsService paymentsService = new PaymentsService(paymentsRepository);

  private final Payment payment = Fixture.payment().build();
  private final List<Payment> paymentList = Collections.singletonList(payment);


  @Test
  void shouldFindAllNonCancelledPayments() {
    when(paymentsRepository.findAllPending()).thenReturn(paymentList);

    List<Payment> actual = paymentsService.findAllPending();

    assertThat(actual, equalTo(paymentList));
    verify(paymentsRepository).findAllPending();
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldFindPaymentById() {
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.of(payment));

    Payment actual = paymentsService.findById(payment.id());

    assertThat(actual, equalTo(payment));
    verify(paymentsRepository).findById(payment.id());
    verifyNoMoreInteractions(paymentsRepository);
  }

  @Test
  void shouldFailToFindPaymentById() {
    when(paymentsRepository.findById(payment.id())).thenReturn(Optional.empty());

    String message = assertThrows(
        ResourceNotFoundException.class,
        () -> paymentsService.findById(payment.id())
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
    when(paymentsRepository.cancel(payment.id())).thenReturn(cancelled);

    Payment actual = paymentsService.cancel(payment.id());

    assertThat(actual, equalTo(cancelled));
    verify(paymentsRepository).cancel(payment.id());
    verifyNoMoreInteractions(paymentsRepository);
  }
}
