package com.gmail.tikrai.payments.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.gmail.tikrai.payments.IntegrationTestCase;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.PaymentCancelFeeResponse;
import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import com.jayway.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class PaymentsControllerIT extends IntegrationTestCase {

  @Autowired
  PaymentsRepository paymentsRepository;

  private final Payment payment = Fixture.payment().build();
  private final String getAllPaymentsPath = String.format("%s/%s", Endpoint.PAYMENTS, "all");
  private final String cancelFeePath = String.format("%s/%s", Endpoint.PAYMENTS, "cancel_fee");
  private final BigDecimal zero = BigDecimal.valueOf(0, 2);

  @Test
  void shouldGetSinglePendingPayment() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().get(getAllPaymentsPath);

    response.then().statusCode(HttpStatus.OK.value());
    Payment[] payments = {actualPayment};
    assertThat(response.as(Payment[].class), equalTo(payments));
  }

  @Test
  void shouldGetSinglePendingPaymentFromRange() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().get(getAllPaymentsPath + "?min=1&max=100");

    response.then().statusCode(HttpStatus.OK.value());
    Payment[] payments = {actualPayment};
    assertThat(response.as(Payment[].class), equalTo(payments));
  }

  @Test
  void shouldGetNoPendingPaymentFromRangeIfRangeIsWrong() {
    paymentsRepository.create(payment);

    Response response = given().get(getAllPaymentsPath + "?min=99&max=100");

    response.then().statusCode(HttpStatus.OK.value());
    Payment[] payments = {};
    assertThat(response.as(Payment[].class), equalTo(payments));
  }

  @Test
  void shouldGetSinglePendingPaymentWhenAnotherIsCanceled() {
    Payment actualPayment = paymentsRepository.create(payment);
    Payment cancelledPayment = paymentsRepository.create(Fixture.payment().amount(5).build());
    paymentsRepository.cancel(cancelledPayment.id(), BigDecimal.ZERO);

    Response response = given().get(getAllPaymentsPath);

    response.then().statusCode(HttpStatus.OK.value());
    Payment[] payments = {actualPayment};
    assertThat(response.as(Payment[].class), equalTo(payments));
  }

  @Test
  void shouldGetPaymentById() {
    Payment actualPayment = paymentsRepository.create(payment);
    String path = String.format("%s/%s", cancelFeePath, actualPayment.id());

    Response response = given().get(path);

    response.then().statusCode(HttpStatus.OK.value());
    PaymentCancelFeeResponse expected =
        new PaymentCancelFeeResponse(actualPayment.id(), true, zero);
    assertThat(response.as(PaymentCancelFeeResponse.class), equalTo(expected));
  }

  @Test
  void shouldCancelPayment() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().delete(
        String.format("%s/%d", Endpoint.PAYMENTS, actualPayment.id()));

    response.then().statusCode(HttpStatus.OK.value());
    Payment actualCancelled = response.as(Payment.class);
    Payment expected = payment.withId(actualCancelled.id()).withCancelled(true).withCancelFee(zero);
    assertThat(actualCancelled, equalTo(expected));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(actualCancelled)));
  }
}
