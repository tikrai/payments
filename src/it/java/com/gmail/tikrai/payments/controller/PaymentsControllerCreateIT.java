package com.gmail.tikrai.payments.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.gmail.tikrai.payments.IntegrationTestCase;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import com.jayway.restassured.response.Response;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class PaymentsControllerCreateIT extends IntegrationTestCase {

  @Autowired
  PaymentsRepository paymentsRepository;

  private final PaymentRequest payment = Fixture.paymentRequest().build();

  @Test
  void shouldCreatePayment() {
    Response response = given().body(payment).post(Endpoint.PAYMENTS);

    response.then().statusCode(HttpStatus.CREATED.value());
    Payment actual = response.as(Payment.class);
    assertThat(actual, equalTo(payment.toDomain().withId(actual.id())));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(actual)));
  }

  @Test
  void shouldFailToCreatePaymentIfRequestIsInvalid() {
    PaymentRequest payment = Fixture.paymentRequest().currency("LTL").build();

    Response response = given().body(payment).post(Endpoint.PAYMENTS);

    response.then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("status", equalTo(400))
        .body("error", equalTo("Bad Request"))
        .body("message", equalTo("'currency' value 'LTL' is not valid"))
        .body("path", equalTo(Endpoint.PAYMENTS));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.emptyList()));
  }
}
