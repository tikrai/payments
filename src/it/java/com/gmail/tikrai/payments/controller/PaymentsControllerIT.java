package com.gmail.tikrai.payments.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.gmail.tikrai.payments.IntegrationTestCase;
import com.gmail.tikrai.payments.domain.CancelFee;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.IdResponse;
import com.gmail.tikrai.payments.response.PaymentResponse;
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

  private final Payment payment = Fixture.payment().build();
  private final BigDecimal zero = BigDecimal.valueOf(0, 2);
  private final Instant now = Instant.now();

  @Test
  void shouldGetSinglePendingPayment() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().get(Endpoint.PAYMENTS);

    response.then().statusCode(HttpStatus.OK.value());
    IdResponse[] payments = {new IdResponse(actualPayment.id())};
    assertThat(response.as(IdResponse[].class), equalTo(payments));
  }

  @Test
  void shouldGetSinglePendingPaymentFromRange() {
    Payment actualPayment = paymentsRepository.create(payment);

    Response response = given().get(String.format("%s?min=1&max=100", Endpoint.PAYMENTS));

    response.then().statusCode(HttpStatus.OK.value());
    IdResponse[] payments = {new IdResponse(actualPayment.id())};
    assertThat(response.as(IdResponse[].class), equalTo(payments));
  }

  @Test
  void shouldGetNoPendingPaymentFromRangeIfRangeIsWrong() {
    paymentsRepository.create(payment);

    Response response = given().get(String.format("%s?min=99&max=100", Endpoint.PAYMENTS));

    response.then().statusCode(HttpStatus.OK.value());
    IdResponse[] payments = {};
    assertThat(response.as(IdResponse[].class), equalTo(payments));
  }

  @Test
  void shouldGetSinglePendingPaymentWhenAnotherIsCanceled() {
    Payment actualPayment = paymentsRepository.create(payment);
    Payment cancelledPayment = paymentsRepository.create(Fixture.payment().amount(5).build());
    CancelFee fee = new CancelFee(cancelledPayment.id(), true, zero, now);
    paymentsRepository.cancel(fee);

    Response response = given().get(Endpoint.PAYMENTS);

    response.then().statusCode(HttpStatus.OK.value());
    IdResponse[] payments = {new IdResponse(actualPayment.id())};
    assertThat(response.as(IdResponse[].class), equalTo(payments));
  }

  @Test
  void shouldFailToGetPaymentsIfRequestIsInvalid() {
    Response response = given().get(Endpoint.PAYMENTS + "?min=-1");

    response.then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("status", equalTo(400))
        .body("error", equalTo("Bad Request"))
        .body("message", equalTo("'min' must be greater than or equal to 0"))
        .body("path", equalTo(Endpoint.PAYMENTS));
  }

  @Test
  void shouldGetPaymentById() {
    Payment actualPayment = paymentsRepository.create(payment);
    String path = String.format("%s/%s", Endpoint.PAYMENTS, actualPayment.id());

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
    PaymentResponse actualCancelled = response.as(PaymentResponse.class);
    Payment expected = Fixture.payment().of(payment)
        .id(actualCancelled.id())
        .cancelled(actualCancelled.cancelled())
        .cancelFee(zero).build();
    assertThat(actualCancelled, equalTo(PaymentResponse.of(expected)));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(expected)));
  }

  @Test
  void shouldFailToCancelPaymentIfNotExists() {
    int id = 42;
    Response response = given()
        .delete(String.format("%s/%d", Endpoint.PAYMENTS, id));

    String expectedMsg = String
        .format("Non cancelled payment with id '%d' was not found", id);
    response.then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("status", equalTo(404))
        .body("error", equalTo("Not Found"))
        .body("message", equalTo(expectedMsg))
        .body("path", equalTo(String.format("%s/%d", Endpoint.PAYMENTS, id)));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.emptyList()));
  }

  @Test
  void shouldFailToCancelPaymentIfAlreadyCancelled() {
    Payment actualPayment = paymentsRepository.create(payment);
    CancelFee fee = new CancelFee(actualPayment.id(), true, zero, now);
    paymentsRepository.cancel(fee);

    Response response = given()
        .delete(String.format("%s/%d", Endpoint.PAYMENTS, actualPayment.id()));

    String expectedMsg = String
        .format("Non cancelled payment with id '%d' was not found", actualPayment.id());
    response.then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("status", equalTo(404))
        .body("error", equalTo("Not Found"))
        .body("message", equalTo(expectedMsg))
        .body("path", equalTo(String.format("%s/%d", Endpoint.PAYMENTS, actualPayment.id())));
    Payment expected = Fixture.payment().of(actualPayment).cancelled(now).cancelFee(zero).build();
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(expected)));
  }
}
