package com.gmail.tikrai.payments.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.CancelFee;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ValidationException;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.response.IdResponse;
import com.gmail.tikrai.payments.service.PaymentsService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PaymentsControllerTest {
  private final PaymentsService paymentsService = mock(PaymentsService.class);
  private final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
  private final PaymentsController paymentsController = new PaymentsController(paymentsService);

  private final PaymentRequest paymentRequest = Fixture.paymentRequest().build();
  private final Payment payment = Fixture.payment().build();
  private final IdResponse paymentId = new IdResponse(payment.id());
  private final List<IdResponse> paymentIdList = Collections.singletonList(paymentId);

  private final BigDecimal zero = BigDecimal.valueOf(0, 2);
  private final BigDecimal one = BigDecimal.valueOf(1, 2);
  private final BigDecimal invalid = BigDecimal.valueOf(0.151);

  @Test
  void shouldFindAllNonCancelledPayments() {
    when(paymentsService.findAllPending(null, null)).thenReturn(paymentIdList);

    ResponseEntity<List<IdResponse>> actual = paymentsController.findAllPending(null, null);

    assertThat(actual, equalTo(new ResponseEntity<>(paymentIdList, HttpStatus.OK)));
    verify(paymentsService).findAllPending(null, null);
  }

  @Test
  void shouldFindFilteredNonCancelledPayments() {
    when(paymentsService.findAllPending(zero, one)).thenReturn(paymentIdList);

    ResponseEntity<List<IdResponse>> actual = paymentsController.findAllPending(zero, one);

    assertThat(actual, equalTo(new ResponseEntity<>(paymentIdList, HttpStatus.OK)));
    verify(paymentsService).findAllPending(zero, one);
  }

  @Test
  void shouldFindFilteredNonCancelledPaymentsIfRangeEndIsSameAsStart() {
    when(paymentsService.findAllPending(one, one)).thenReturn(paymentIdList);

    ResponseEntity<List<IdResponse>> actual = paymentsController.findAllPending(one, one);

    assertThat(actual, equalTo(new ResponseEntity<>(paymentIdList, HttpStatus.OK)));
    verify(paymentsService).findAllPending(one, one);
  }

  @Test
  void shouldFailToFindFilteredNonCancelledPaymentsIfRangeStartIsInvalid() {

    String message = assertThrows(
        ValidationException.class,
        () -> paymentsController.findAllPending(invalid, null)
    ).getMessage();

    assertThat(message, equalTo("'min' value '0.151' must be rounded to 2 decimal digits"));
  }

  @Test
  void shouldFailToFindFilteredNonCancelledPaymentsIfRangeEndIsInvalid() {

    String message = assertThrows(
        ValidationException.class,
        () -> paymentsController.findAllPending(null, invalid)
    ).getMessage();

    assertThat(message, equalTo("'max' value '0.151' must be rounded to 2 decimal digits"));
  }

  @Test
  void shouldFailToFindFilteredNonCancelledPaymentsIfMinIsNegative() {
    String message = assertThrows(
        ValidationException.class,
        () -> paymentsController.findAllPending(BigDecimal.valueOf(-1.1), null)
    ).getMessage();

    assertThat(message, equalTo("'min' must be greater than or equal to 0"));
  }

  @Test
  void shouldFailToFindFilteredNonCancelledPaymentsIfMaxIsNegative() {
    String message = assertThrows(
        ValidationException.class,
        () -> paymentsController.findAllPending(null, BigDecimal.valueOf(-1.1))
    ).getMessage();

    assertThat(message, equalTo("'max' must be greater than or equal to 0"));
  }

  @Test
  void shouldFailToFindFilteredNonCancelledPaymentsIfMinIsMoreThanMax() {
    String message = assertThrows(
        ValidationException.class,
        () -> paymentsController.findAllPending(one, zero)
    ).getMessage();

    assertThat(message, equalTo("'max' must be greater than or equal than 'min'"));
  }

  @Test
  void shouldFindPaymentCancellingFeeById() {
    CancelFee paymentResponse = new CancelFee(1, true, one, Instant.now());
    when(paymentsService.getCancellingFee(payment.id())).thenReturn(paymentResponse);

    ResponseEntity<CancelFee> actual = paymentsController.getCancellingFee(payment.id());

    assertThat(actual, equalTo(new ResponseEntity<>(paymentResponse, HttpStatus.OK)));
    verify(paymentsService).getCancellingFee(payment.id());
  }

  @Test
  void shouldCreateNewPayment() {
    when(paymentsService.create(any(Payment.class))).thenReturn(payment);

    ResponseEntity<Payment> actual = paymentsController.create(paymentRequest, httpServletRequest);

    Payment expected = payment.withCreated(actual.getBody().created());
    assertThat(actual, equalTo(new ResponseEntity<>(expected, HttpStatus.CREATED)));
    verify(paymentsService).create(any(Payment.class));
    verify(httpServletRequest).getRemoteAddr();
  }

  @Test
  void shouldFailToCreateNewPaymentIfRequestIsInvalid() {
    PaymentRequest request = Fixture.paymentRequest().currency("EUR1").build();

    String message = assertThrows(
        ValidationException.class,
        () -> paymentsController.create(request, httpServletRequest)
    ).getMessage();

    assertThat(message, equalTo("'currency' value 'EUR1' is not valid"));
  }

  @Test
  void shouldCancelPayment() {
    Payment cancelled = payment.withCancelled(Instant.now());
    when(paymentsService.cancel(payment.id())).thenReturn(cancelled);

    ResponseEntity<Payment> actual = paymentsController.cancel(payment.id());

    assertThat(actual, equalTo(new ResponseEntity<>(cancelled, HttpStatus.OK)));
    verify(paymentsService).cancel(payment.id());
  }

  @AfterEach
  void verifyMocks() {
    verifyNoMoreInteractions(paymentsService, httpServletRequest);
  }
}
