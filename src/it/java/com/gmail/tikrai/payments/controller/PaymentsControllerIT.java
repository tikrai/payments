package com.gmail.tikrai.payments.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.gmail.tikrai.payments.IntegrationTestCase;
import com.gmail.tikrai.payments.domain.CancelFee;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.IdResponse;
import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import com.jayway.restassured.response.Response;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class PaymentsControllerIT extends IntegrationTestCase {

  @Autowired
  PaymentsRepository paymentsRepository;

  private final Payment payment = Fixture.payment().cancelled(null).build();
  private final String getAllPaymentsPath = String.format("%s/%s", Endpoint.PAYMENTS, "all");
  private final String cancelFeePath = String.format("%s/%s", Endpoint.PAYMENTS, "cancel_fee");
  private final BigDecimal zero = BigDecimal.valueOf(0, 2);

  @Test
  void shouldGetSinglePendingPayment() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().get(getAllPaymentsPath);

    response.then().statusCode(HttpStatus.OK.value());
    IdResponse[] payments = {new IdResponse(actualPayment.id())};
    assertThat(response.as(IdResponse[].class), equalTo(payments));
  }

  @Test
  void shouldGetSinglePendingPaymentFromRange() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().get(getAllPaymentsPath + "?min=1&max=100");

    response.then().statusCode(HttpStatus.OK.value());
    IdResponse[] payments = {new IdResponse(actualPayment.id())};
    assertThat(response.as(IdResponse[].class), equalTo(payments));
  }

  @Test
  void shouldGetNoPendingPaymentFromRangeIfRangeIsWrong() {
    paymentsRepository.create(payment);

    Response response = given().get(getAllPaymentsPath + "?min=99&max=100");

    response.then().statusCode(HttpStatus.OK.value());
    IdResponse[] payments = {};
    assertThat(response.as(IdResponse[].class), equalTo(payments));
  }

  @Test
  void shouldGetSinglePendingPaymentWhenAnotherIsCanceled() {
    Payment actualPayment = paymentsRepository.create(payment);
    Payment cancelledPayment = paymentsRepository.create(Fixture.payment().amount(5).build());
    CancelFee fee = new CancelFee(cancelledPayment.id(), true, zero, Instant.now());
    paymentsRepository.cancel(fee);

    Response response = given().get(getAllPaymentsPath);

    response.then().statusCode(HttpStatus.OK.value());
    IdResponse[] payments = {new IdResponse(actualPayment.id())};
    assertThat(response.as(IdResponse[].class), equalTo(payments));
  }

  @Test
  void shouldGetPaymentById() {
    Payment actualPayment = paymentsRepository.create(payment);
    String path = String.format("%s/%s", cancelFeePath, actualPayment.id());

    Response response = given().get(path);

    response.then().statusCode(HttpStatus.OK.value());
    CancelFee actual = response.as(CancelFee.class);
    CancelFee expected = new CancelFee(actualPayment.id(), true, zero, actual.time());
    assertThat(actual, equalTo(expected));
  }

  @Test
  void shouldCancelPayment() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given()
        .delete(String.format("%s/%d", Endpoint.PAYMENTS, actualPayment.id()));

    response.then().statusCode(HttpStatus.OK.value());
    Payment actualCancelled = response.as(Payment.class);
    Payment expected = Fixture.payment().of(payment)
        .id(actualCancelled.id())
        .cancelled(actualCancelled.cancelled().get())
        .cancelFee(zero).build();
    assertThat(actualCancelled, equalTo(expected));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(actualCancelled)));
  }
}
