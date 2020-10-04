package com.gmail.tikrai.payments.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.CancelFee;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.domain.Payment.Type;
import com.gmail.tikrai.payments.exception.ConflictException;
import com.gmail.tikrai.payments.exception.ResourceNotFoundException;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.IdResponse;
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
  private final RestService restService = mock(RestService.class);
  private final TimeService timeService = mock(TimeService.class);
  private final String timezone = "Europe/Vilnius";
  private final PaymentsService paymentsService =
      new PaymentsService(paymentsRepository, restService, timeService, timezone);

  private final Payment payment = Fixture.payment().cancelCoeff(5).build();
  private final Instant now = Instant.now();
  private final Instant createTime = Instant.parse("2020-09-30T18:33:47.053Z");
  private final IdResponse paymentId = new IdResponse(payment.id());
  private final BigDecimal zero = BigDecimal.valueOf(0, 2);
  private final CancelFee fee = new CancelFee(payment.id(), true, zero, now);


  @Test
  void shouldFindAllNonCancelledPayments() {
    List<Payment> paymentList = Collections.singletonList(payment);
    when(paymentsRepository.findAllPending(null, null)).thenReturn(paymentList);

    List<IdResponse> actual = paymentsService.findAllPending(null, null);

    List<IdResponse> expected = Collections.singletonList(paymentId);
    assertThat(actual, equalTo(expected));
    verify(paymentsRepository).findAllPending(null, null);
  }

  @Test
  void shouldGetCancellingFeeByIdForTwoHourOldPayment() {
    int hours = 2;
    Instant requestTime = createTime.plus(Duration.ofHours(hours));
    Payment payment = Fixture.payment().type(Type.TYPE1).cancelCoeff(5).created(createTime).build();
    when(timeService.now()).thenReturn(requestTime);
    when(paymentsRepository.findNonCancelledById(payment.id())).thenReturn(Optional.of(payment));

    CancelFee actualResponse = paymentsService.getCancellingFee(payment.id());

    BigDecimal expectedFee = BigDecimal.valueOf(payment.cancelCoeff().get() * hours, 2);
    CancelFee expectedResponse = new CancelFee(0, true, expectedFee, requestTime);
    assertThat(actualResponse, equalTo(expectedResponse));
    verify(paymentsRepository).findNonCancelledById(payment.id());
    verify(timeService).now();
  }

  @Test
  void shouldGetCancellingFeeByIdForAlmostHourOldPayment() {
    Instant requestTime = createTime.plus(Duration.ofHours(1).minus(Duration.ofSeconds(1)));
    Payment payment = Fixture.payment().of(this.payment).created(createTime).build();
    when(timeService.now()).thenReturn(requestTime);
    when(paymentsRepository.findNonCancelledById(payment.id())).thenReturn(Optional.of(payment));

    CancelFee actualResponse = paymentsService.getCancellingFee(payment.id());

    BigDecimal expectedFee = BigDecimal.valueOf(0, 2);
    CancelFee expectedResponse = new CancelFee(0, true, expectedFee, requestTime);
    assertThat(actualResponse, equalTo(expectedResponse));
    verify(paymentsRepository).findNonCancelledById(payment.id());
    verify(timeService).now();
  }

  @Test
  void shouldGetCancellingNotPossibleById() {
    when(timeService.now()).thenReturn(now);
    Payment oldPayment = payment.withCreated(now.minus(Duration.ofHours(25)));
    when(paymentsRepository.findNonCancelledById(oldPayment.id()))
        .thenReturn(Optional.of(oldPayment));

    CancelFee actual = paymentsService.getCancellingFee(oldPayment.id());

    CancelFee expected = new CancelFee(0, false, null, now);
    assertThat(actual, equalTo(expected));
    verify(paymentsRepository).findNonCancelledById(payment.id());
    verify(timeService).now();
  }

  @Test
  void shouldFailToGetCancellingFeeByIdIfNotExists() {
    when(paymentsRepository.findNonCancelledById(payment.id())).thenReturn(Optional.empty());

    String message = assertThrows(
        ResourceNotFoundException.class,
        () -> paymentsService.getCancellingFee(payment.id())
    ).getMessage();

    String expectedMessage =
        String.format("Non cancelled payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findNonCancelledById(payment.id());
  }

  @Test
  void shouldCreateNewPayment() {
    when(timeService.now()).thenReturn(now);
    Payment paymentNow = payment.withCreated(now);
    when(paymentsRepository.create(paymentNow)).thenReturn(paymentNow);

    Payment actual = paymentsService.create(payment);

    assertThat(actual, equalTo(paymentNow));
    verify(paymentsRepository).create(paymentNow);
    verify(restService).resolveIpAdress(paymentNow);
    verify(restService).notifyPaymentSaved(paymentNow);
    verify(timeService).now();
  }

  @Test
  void shouldCancelPayment() {
    Payment cancelled = Fixture.payment().of(payment).cancelled(Instant.now()).build();
    when(paymentsRepository.findNonCancelledById(payment.id()))
        .thenReturn(Optional.of(Fixture.payment().of(payment).cancelFee(zero).build()));
    when(timeService.now()).thenReturn(now);
    when(paymentsRepository.cancel(fee)).thenReturn(Optional.of(cancelled));

    Payment actual = paymentsService.cancel(payment.id());

    assertThat(actual, equalTo(cancelled));
    verify(paymentsRepository).findNonCancelledById(payment.id());
    verify(timeService).now();
    verify(paymentsRepository).cancel(fee);
  }

  @Test
  void shouldFailToCancelPaymentIfNotExists() {
    when(paymentsRepository.findNonCancelledById(payment.id())).thenReturn(Optional.empty());

    String message = assertThrows(
        ResourceNotFoundException.class,
        () -> paymentsService.cancel(payment.id())
    ).getMessage();

    String expectedMessage =
        String.format("Non cancelled payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findNonCancelledById(payment.id());
  }

  @Test
  void shouldFailToCancelPaymentIfAfterCalculationDoNotExistAnymore() {
    when(paymentsRepository.findNonCancelledById(payment.id())).thenReturn(Optional.of(payment));
    when(timeService.now()).thenReturn(now);
    when(paymentsRepository.cancel(fee)).thenReturn(Optional.empty());

    String message = assertThrows(
        ResourceNotFoundException.class,
        () -> paymentsService.cancel(payment.id())
    ).getMessage();

    String expectedMessage =
        String.format("Non cancelled payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findNonCancelledById(payment.id());
    verify(timeService).now();
    verify(paymentsRepository).cancel(fee);
  }

  @Test
  void shouldFailToCancelPaymentIfPeriodExpired() {
    when(timeService.now()).thenReturn(now);
    Payment expired = payment.withCreated(now.minus(Duration.ofHours(25)));
    when(paymentsRepository.findNonCancelledById(payment.id())).thenReturn(Optional.of(expired));

    String message = assertThrows(
        ConflictException.class,
        () -> paymentsService.cancel(payment.id())
    ).getMessage();

    String expectedMessage = String.format("Not possible to cancel payment '%s'", payment.id());
    assertThat(message, equalTo(expectedMessage));
    verify(paymentsRepository).findNonCancelledById(payment.id());
    verify(timeService).now();
  }

  @AfterEach
  void verifyMocks() {
    verifyNoMoreInteractions(paymentsRepository, restService, timeService);
  }
}
