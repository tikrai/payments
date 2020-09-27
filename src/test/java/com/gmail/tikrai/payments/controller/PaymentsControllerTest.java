package com.gmail.tikrai.payments.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ValidationException;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.response.PaymentCancelFeeResponse;
import com.gmail.tikrai.payments.service.PaymentsService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PaymentsControllerTest {
  private final PaymentsService paymentsService = mock(PaymentsService.class);
  private final PaymentsController paymentsController = new PaymentsController(paymentsService);

  private final PaymentRequest paymentRequest = Fixture.paymentRequest().build();
  private final Payment payment = Fixture.payment().build();
  private final List<Payment> paymentList = Collections.singletonList(payment);

  @Test
  void shouldFindAllNonCancelledPayments() {
    when(paymentsService.findAllPending()).thenReturn(paymentList);

    ResponseEntity<List<Payment>> actual = paymentsController.findAllPending();

    assertThat(actual, equalTo(new ResponseEntity<>(paymentList, HttpStatus.OK)));
    verify(paymentsService).findAllPending();
    verifyNoMoreInteractions(paymentsService);
  }

  @Test
  void shouldFindPaymentCancellingFeeById() {
    PaymentCancelFeeResponse paymentResponse =
        new PaymentCancelFeeResponse(1, true, BigDecimal.ONE);
    when(paymentsService.getCancellingFee(payment.id())).thenReturn(paymentResponse);

    ResponseEntity<PaymentCancelFeeResponse> actual =
        paymentsController.getCancellingFee(payment.id());

    assertThat(actual, equalTo(new ResponseEntity<>(paymentResponse, HttpStatus.OK)));
    verify(paymentsService).getCancellingFee(payment.id());
    verifyNoMoreInteractions(paymentsService);
  }

  @Test
  void shouldCreateNewPayment() {
    when(paymentsService.create(any(Payment.class))).thenReturn(payment);

    ResponseEntity<Payment> actual = paymentsController.create(paymentRequest);

    Payment expected = this.payment.withCreated(actual.getBody().created());
    assertThat(actual, equalTo(new ResponseEntity<>(expected, HttpStatus.CREATED)));
    verify(paymentsService).create(any(Payment.class));
    verifyNoMoreInteractions(paymentsService);
  }

  @Test
  void shouldFailToCreateNewPaymentIfRequestIsInvalid() {
    PaymentRequest request = Fixture.paymentRequest().currency("EUR1").build();

    String message = assertThrows(
        ValidationException.class,
        () -> paymentsController.create(request)
    ).getMessage();

    assertThat(message, equalTo("'currency' value 'EUR1' is not valid"));
    verifyNoMoreInteractions(paymentsService);
  }

  @Test
  void shouldCancelPayment() {
    Payment cancelled = payment.withCancelled(true);
    when(paymentsService.cancel(payment.id())).thenReturn(cancelled);

    ResponseEntity<Payment> actual = paymentsController.cancel(payment.id());

    assertThat(actual, equalTo(new ResponseEntity<>(cancelled, HttpStatus.OK)));
    verify(paymentsService).cancel(payment.id());
    verifyNoMoreInteractions(paymentsService);
  }
}
