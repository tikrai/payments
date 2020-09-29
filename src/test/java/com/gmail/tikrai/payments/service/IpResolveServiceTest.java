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
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class IpResolveServiceTest {

  private final RestTemplate restTemplate = mock(RestTemplate.class);
  private final PaymentsRepository paymentsRepository = mock(PaymentsRepository.class);
  private final String ipResolveApiUrl = "www.get/%s";
  private final IpResolveService ipResolveService =
      new IpResolveService(restTemplate, paymentsRepository, ipResolveApiUrl);

  private final Payment payment = Fixture.payment().build();

  @Test
  void shouldFindAllNonCancelledPayments() {
    IpApiResponse response = new IpApiResponse("Paylandia");
    when(restTemplate.getForObject(anyString(), eq(IpApiResponse.class))).thenReturn(response);

    ipResolveService.resolveIpAdress(payment);

    String expected = String.format(ipResolveApiUrl, payment.ipAddress().get());
    verify(restTemplate).getForObject(expected, IpApiResponse.class);
    verify(paymentsRepository).logCountry(payment.id(), response.country());
    verifyNoMoreInteractions(restTemplate, paymentsRepository);
  }
}
