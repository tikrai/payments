package com.gmail.tikrai.payments.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.gmail.tikrai.payments.IntegrationTestCase;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import com.jayway.restassured.response.Response;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class PaymentsControllerIT extends IntegrationTestCase {

  @Autowired
  PaymentsRepository paymentsRepository;

  private final Payment payment = Fixture.payment().build();
  private final String getAllPaymentsPath = String.format("%s/%s", Endpoint.PAYMENTS, "all");
  private final String getPaymentPath = String.format("%s/%s", Endpoint.PAYMENTS, "get");

  @Test
  void shouldGetSinglePendingPayment() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().get(getAllPaymentsPath);

    response.then().statusCode(HttpStatus.OK.value());
    Payment[] payments = {actualPayment};
    assertThat(response.as(Payment[].class), equalTo(payments));
  }

  @Test
  void shouldGetSinglePendingPaymentWhenAnotherIsCanceled() {
    Payment actualPayment = paymentsRepository.create(payment);
    Payment cancelledPayment = paymentsRepository.create(Fixture.payment().amount(5).build());
    paymentsRepository.cancel(cancelledPayment.id());

    Response response = given().get(getAllPaymentsPath);

    response.then().statusCode(HttpStatus.OK.value());
    Payment[] payments = {actualPayment};
    assertThat(response.as(Payment[].class), equalTo(payments));
  }

  @Test
  void shouldGetPaymentById() {
    Payment actualPayment = paymentsRepository.create(payment);
    String path = String.format("%s/%s", getPaymentPath, actualPayment.id());

    Response response = given().get(path);

    response.then().statusCode(HttpStatus.OK.value());
    assertThat(response.as(Payment.class), equalTo(actualPayment));
  }

  @Test
  void shouldCreatePayment() {
    Response response = given().body(payment).post(Endpoint.PAYMENTS);

    response.then().statusCode(HttpStatus.CREATED.value());
    Payment actual = response.as(Payment.class);
    assertThat(actual, equalTo(payment.withId(actual.id())));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(actual)));
  }

  @Test
  void shouldCancelPayment() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().delete(
        String.format("%s/%d", Endpoint.PAYMENTS, actualPayment.id()));

    response.then().statusCode(HttpStatus.OK.value());
    Payment actualCancelled = response.as(Payment.class);
    assertThat(actualCancelled, equalTo(payment.withId(actualCancelled.id()).withCancelled(true)));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(actualCancelled)));
  }
}
