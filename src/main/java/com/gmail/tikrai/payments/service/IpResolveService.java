package com.gmail.tikrai.payments.service;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.IpApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IpResolveService {

  private final RestTemplate restTemplate;
  private final PaymentsRepository paymentsRepository;
  private final String ipResolveApiUrl;

  @Autowired
  public IpResolveService(
      RestTemplate restTemplate,
      PaymentsRepository paymentsRepository,
      @Value("${payments.ipResolveApiUrl}") String ipResolveApiUrl
  ) {
    this.restTemplate = restTemplate;
    this.paymentsRepository = paymentsRepository;
    this.ipResolveApiUrl = ipResolveApiUrl;
  }

  @Async
  public void resolveIpAdress(Payment payment) {
    String url = String.format(ipResolveApiUrl, payment.ipAddress().get());
    IpApiResponse response = restTemplate.getForObject(url, IpApiResponse.class);
    paymentsRepository.logCountry(payment.id(), response.country());
  }
}
