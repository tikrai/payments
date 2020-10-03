package com.gmail.tikrai.payments.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.IpApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class RestServiceTest {

  private final RestTemplate restTemplate = mock(RestTemplate.class);
  private final PaymentsRepository paymentsRepository = mock(PaymentsRepository.class);
  private final String ipResolveApiUrl = "www.get/%s";
  private final String notifyApi = "http://numbersapi.com";
  private final RestService restService =
      new RestService(restTemplate, paymentsRepository, ipResolveApiUrl, notifyApi);

  private final Payment payment = Fixture.payment().build();

  @Test
  void shouldNotifyPaymentSaved() {
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
    when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

    restService.notifyPaymentSaved(payment);

    String expected = String.format("%s/%s", notifyApi, payment.id());
    verify(restTemplate).getForEntity(expected, String.class);
    verify(paymentsRepository).logNotified(payment.id(), true);
  }

  @Test
  void shouldNotifyPaymentNotSaved() {
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

    restService.notifyPaymentSaved(payment);

    String expected = String.format("%s/%s", notifyApi, payment.id());
    verify(restTemplate).getForEntity(expected, String.class);
    verify(paymentsRepository).logNotified(payment.id(), false);
  }

  @Test
  void shouldResolveIpAddress() {
    IpApiResponse response = new IpApiResponse("Paylandia");
    when(restTemplate.getForObject(anyString(), eq(IpApiResponse.class))).thenReturn(response);

    restService.resolveIpAdress(payment);

    String expected = String.format(ipResolveApiUrl, payment.ipAddress().get());
    verify(restTemplate).getForObject(expected, IpApiResponse.class);
    verify(paymentsRepository).logCountry(payment.id(), response.country());
  }

  @Test
  void shouldFailToResolveIpAddress() {
    when(restTemplate.getForObject(anyString(), eq(IpApiResponse.class))).thenReturn(null);

    restService.resolveIpAdress(payment);

    String expected = String.format(ipResolveApiUrl, payment.ipAddress().get());
    verify(restTemplate).getForObject(expected, IpApiResponse.class);
  }

  @AfterEach
  void verifyMocks() {
    verifyNoMoreInteractions(restTemplate, paymentsRepository);
  }
}
