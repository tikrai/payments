package com.gmail.tikrai.payments;

import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTestCase {

  @Value("${local.server.port}")
  private int port;

  @Autowired
  private JdbcOperations db;

  @Autowired
  public RestTemplate restTemplate;

  protected MockRestServiceServer mockServer;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
    flushTables(PaymentsRepository.TABLE);
    mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
  }

  private void flushTables(String... tables) {
    String joinedTableNames = String.join(", ", tables);
    String sql = String.format("DELETE FROM %s", joinedTableNames);
    db.update(sql);
  }

  protected RequestSpecification given() {
    return RestAssured.given().contentType(ContentType.JSON);
  }
}
