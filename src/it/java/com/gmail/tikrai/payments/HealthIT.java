package com.gmail.tikrai.payments;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class HealthIT extends IntegrationTestCase {

  @Value("${payments.ipResolveApiUrl}")
  private String ipResolveApiUrl;

  @Value("${payments.notifyApi}")
  private String notifyApi;

  @Test
  void shouldShowHealth() {
    mockServer.expect(requestTo(String.format(ipResolveApiUrl, "1")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("OK", MediaType.APPLICATION_JSON));
    mockServer.expect(requestTo(notifyApi))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("OK", MediaType.APPLICATION_JSON));

    Response response = given().get(Endpoint.HEALTH);

    response.then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body("status", equalTo("UP"))
        .body("components.db.status", equalTo("UP"))
        .body("components.diskSpace.status", equalTo("UP"))
        .body("components.ipResolveApi.status", equalTo("UP"))
        .body("components.notificationApi.status", equalTo("UP"))
        .body("components.ping.status", equalTo("UP"));
  }
}
