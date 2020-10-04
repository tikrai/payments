package com.gmail.tikrai.payments.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.gmail.tikrai.payments.IntegrationTestCase;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.response.PaymentResponse;
import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import com.jayway.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ResponseCreator;

class PaymentsControllerCreateIT extends IntegrationTestCase {

  @Autowired
  PaymentsRepository paymentsRepository;

  private final PaymentRequest paymentRequest = Fixture.paymentRequest().bicCode(null).build();
  private final SleepThread ipApiThread = new SleepThread(200);
  private final SleepThread notifyApiThread = new SleepThread(200);

  private void expectRequestsAndRespondWithSuccess(
      boolean ipApiResponseSuccess,
      boolean notifyApiResponseSuccess
  ) {
    String ipApiResponse = "{\"country\": \"Paylandia\"}";
    String notifyApiResponse = "anything";
    String ipApiFormat = "(http://ip-api.com/json/).+";
    String notifyApiFormat = "(http://numbersapi.com/).+";
    mockServer.expect(requestTo(matchesRegex(ipApiFormat)))
        .andExpect(method(HttpMethod.GET))
        .andRespond(delayedResponse(ipApiResponse, ipApiThread, ipApiResponseSuccess));
    mockServer.expect(requestTo(matchesRegex(notifyApiFormat)))
        .andExpect(method(HttpMethod.GET))
        .andRespond(delayedResponse(notifyApiResponse, notifyApiThread, notifyApiResponseSuccess));
  }

  @Test
  void shouldCreatePaymentFastWhenApisAreSlow() throws InterruptedException {
    expectRequestsAndRespondWithSuccess(true, true);

    Response response = given().body(paymentRequest).post(Endpoint.PAYMENTS);

    response.then().statusCode(HttpStatus.CREATED.value());
    PaymentResponse actual = response.as(PaymentResponse.class);
    Payment expected = paymentRequest.toDomain("127.0.0.1")
        .withId(actual.id())
        .withCreated(actual.created());
    assertThat(actual, equalTo(PaymentResponse.of(expected)));
    verifyDbRecords(expected, "Paylandia", true);
  }

  @Test
  void shouldCreatePaymentFastWhenApisAreSlowAndIpResolveFails() throws InterruptedException {
    expectRequestsAndRespondWithSuccess(false, true);

    Response response = given().body(paymentRequest).post(Endpoint.PAYMENTS);

    response.then().statusCode(HttpStatus.CREATED.value());
    PaymentResponse actual = response.as(PaymentResponse.class);
    Payment expected = paymentRequest.toDomain("127.0.0.1")
        .withId(actual.id())
        .withCreated(actual.created());
    assertThat(actual, equalTo(PaymentResponse.of(expected)));
    verifyDbRecords(expected, null, true);
  }

  @Test
  void shouldCreatePaymentFastWhenApisAreSlowAndNotifyFails() throws InterruptedException {
    expectRequestsAndRespondWithSuccess(true, false);

    Response response = given().body(paymentRequest).post(Endpoint.PAYMENTS);

    response.then().statusCode(HttpStatus.CREATED.value());
    PaymentResponse actual = response.as(PaymentResponse.class);
    Payment expected = paymentRequest.toDomain("127.0.0.1")
        .withId(actual.id())
        .withCreated(actual.created());
    assertThat(actual, equalTo(PaymentResponse.of(expected)));
    verifyDbRecords(expected, "Paylandia", false);
  }

  private void verifyDbRecords(
      Payment payment,
      String country,
      Boolean notified
  ) throws InterruptedException {
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(payment)));
    Payment afterUpdate = Fixture.payment().of(payment).country(country).notified(notified).build();
    Arrays.asList(ipApiThread, notifyApiThread).forEach(Thread::interrupt);
    Thread.sleep(400);
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(afterUpdate)));
  }

  private ResponseCreator delayedResponse(String apiResponse, Thread sleepThread, boolean success) {
    return request -> {
      sleepThread.start();
      try {
        sleepThread.join();
      } catch (InterruptedException ignored) {
        //ignored
      }
      return success
          ? withSuccess(apiResponse, MediaType.APPLICATION_JSON).createResponse(request)
          : withServerError().createResponse(request);
    };
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

  @Test
  void shouldFailToCreatePaymentIfRequestHasUnknownField() {
    String payment = "{\n"
        + "    \"type\": \"TYPE2\",\n"
        + "    \"amount\": 500000.00,\n"
        + "    \"currency\": \"USD\",\n"
        + "    \"debtor_iban\": \"from me\",\n"
        + "    \"creditor_iban\": \"to you\",\n"
        + "    \"other\": 11\n"
        + "}";

    Response response = given().body(payment).post(Endpoint.PAYMENTS);

    response.then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("status", equalTo(400))
        .body("error", equalTo("Bad Request"))
        .body("message", startsWith("JSON parse error: Unrecognized field \"other\""))
        .body("path", equalTo(Endpoint.PAYMENTS));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.emptyList()));
  }

  @AfterEach
  void verifyMockserver() {
    mockServer.verify();
  }

  private static class SleepThread extends Thread {

    private final int delay;

    private SleepThread(int delay) {
      this.delay = delay;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(delay);
      } catch (InterruptedException ignored) {
        //ignored
      }
    }
  }
}