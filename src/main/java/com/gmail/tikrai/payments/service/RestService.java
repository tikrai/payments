package com.gmail.tikrai.payments.service;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.domain.Payment.Type;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import com.gmail.tikrai.payments.response.IpApiResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

  private final RestTemplate restTemplate;
  private final PaymentsRepository paymentsRepository;
  private final String ipResolveApiUrl;

  private Map<Type, Optional<String>> notifyEndpoints = new HashMap<>();

  @Autowired
  public RestService(
      RestTemplate restTemplate,
      PaymentsRepository paymentsRepository,
      @Value("${payments.ipResolveApiUrl}") String ipResolveApiUrl,
      @Value("${payments.notifyApi}") String notifyApi
  ) {
    this.restTemplate = restTemplate;
    this.paymentsRepository = paymentsRepository;
    this.ipResolveApiUrl = ipResolveApiUrl;
    notifyEndpoints.put(Type.TYPE1, Optional.of(String.format("%s/%%s", notifyApi)));
    notifyEndpoints.put(Type.TYPE2, Optional.of(String.format("%s/%%s/math", notifyApi)));
    notifyEndpoints.put(Type.TYPE3, Optional.empty());
  }

  @Async
  public void notifyPaymentSaved(Payment payment) {
    notifyEndpoints.get(payment.type())
        .map(endpointFormat -> String.format(endpointFormat, payment.id()))
        .map(endpointUrl -> restTemplate.getForEntity(endpointUrl, String.class))
        .map(ResponseEntity::getStatusCode)
        .map(HttpStatus::is2xxSuccessful)
        .ifPresent(statusIsOk -> paymentsRepository.logNotified(payment.id(), statusIsOk));
  }

  @Async
  public void resolveIpAdress(Payment payment) {
    String url = String.format(ipResolveApiUrl, payment.ipAddress().get());
    IpApiResponse response = restTemplate.getForObject(url, IpApiResponse.class);
    if (response != null) {
      paymentsRepository.logCountry(payment.id(), response.country());
    }
  }
}
