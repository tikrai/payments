package com.gmail.tikrai.payments.controller;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.domain.PaymentRequest;
import com.gmail.tikrai.payments.service.PaymentsService;
import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoint.PAYMENTS)
public class PaymentsController {

  private final PaymentsService paymentsService;

  public PaymentsController(PaymentsService paymentsService) {
    this.paymentsService = paymentsService;
  }

  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Payment>> getAllPending() {
    return new ResponseEntity<>(paymentsService.findAllPending(), HttpStatus.OK);
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Payment> getById(
      @PathVariable Integer id
  ) {
    return new ResponseEntity<>(paymentsService.findById(id), HttpStatus.OK);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Payment> create(
      @RequestBody PaymentRequest request
  ) {
    return new ResponseEntity<>(paymentsService.create(request.toDomain()), HttpStatus.CREATED);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Payment> delete(
      @PathVariable("id") int id
  ) {
    return new ResponseEntity<>(paymentsService.cancel(id), HttpStatus.OK);
  }
}
