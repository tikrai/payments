package com.gmail.tikrai.payments.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.gmail.tikrai.payments.IntegrationTestCase;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import com.jayway.restassured.response.Response;
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

  private final PaymentRequest payment = Fixture.paymentRequest().build();
  private final String ipApiResponse = "{\"country\": \"Luminoria\",\"countryCode\": \"LT\"}";
  private final String notifyApiResponse = "anything";
  private final SleepThread ipApiThread = new SleepThread(200);
  private final SleepThread notifyApiThread = new SleepThread(200);
  private final String ipApiFormat = "(http://ip-api.com/json/).+";
  private final String notifyApiFormat = "(http://numbersapi.com/).+";

  @Test
  void shouldCreatePaymentFastWhenApisAreSlow() throws InterruptedException {
    mockServer.expect(requestTo(matchesRegex(ipApiFormat)))
        .andExpect(method(HttpMethod.GET))
        .andRespond(delayedResponse(ipApiThread, ipApiResponse));
    mockServer.expect(requestTo(matchesRegex(notifyApiFormat)))
        .andExpect(method(HttpMethod.GET))
        .andRespond(delayedResponse(notifyApiThread, notifyApiResponse));

    Response response = given().body(payment).post(Endpoint.PAYMENTS);

    response.then().statusCode(HttpStatus.CREATED.value());
    Payment actual = response.as(Payment.class);
    Payment expectedBeforeUpdate = payment.toDomain("127.0.0.1")
        .withId(actual.id())
        .withCreated(actual.created());
    assertThat(actual, equalTo(expectedBeforeUpdate));
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(actual)));
    ipApiThread.interrupt();
    notifyApiThread.interrupt();
    Thread.sleep(400);
    Payment afterUpdate = Fixture.payment().of(expectedBeforeUpdate)
        .country("Luminoria")
        .notified(true).build();
    assertThat(paymentsRepository.findAll(), equalTo(Collections.singletonList(afterUpdate)));
    //todo extract delay labdas and add test create payment with unsuccessfull requests to apis
  }

  private ResponseCreator delayedResponse(SleepThread sleepThread, String apiResponse) {
    return request -> {
      sleepThread.start();
      try {
        sleepThread.join();
      } catch (InterruptedException ignored) {
        //ignored
      }
      return withSuccess(apiResponse, MediaType.APPLICATION_JSON).createResponse(request);
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


  @AfterEach
  void verifyMockserver() {
    mockServer.verify();
  }

  private static class SleepThread extends Thread {

    private final int delay;

    public SleepThread(int delay) {
      this.delay = delay;
    }

    public void run() {
      try {
        Thread.sleep(delay);
      } catch (InterruptedException ignored) {
        //ignored
      }
    }
  }
}