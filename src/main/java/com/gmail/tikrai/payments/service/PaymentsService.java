package com.gmail.tikrai.payments.service;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ResourceNotFoundException;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentsService {

  private final PaymentsRepository paymentsRepository;

  @Autowired
  public PaymentsService(PaymentsRepository paymentsRepository) {
    this.paymentsRepository = paymentsRepository;
  }

  public List<Payment> findAllPending() {
    return paymentsRepository.findAllPending();
  }

  public Payment findById(int id) {
    return paymentsRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Payment with id '%s' was not found", id)
        ));
  }

  public Payment create(Payment payment) {
    return paymentsRepository.create(payment);
  }

  public Payment cancel(int id) {
    return paymentsRepository.cancel(id);
  }
}
