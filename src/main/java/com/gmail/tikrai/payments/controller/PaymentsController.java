package com.gmail.tikrai.payments.controller;

import static java.math.BigDecimal.ROUND_UNNECESSARY;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.response.IdResponse;
import com.gmail.tikrai.payments.response.PaymentCancelFeeResponse;
import com.gmail.tikrai.payments.service.PaymentsService;
import com.gmail.tikrai.payments.util.RestUtil.Endpoint;
import com.gmail.tikrai.payments.validation.validators.DecimalValidator;
import com.gmail.tikrai.payments.validation.validators.SizeValidator;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoint.PAYMENTS)
public class PaymentsController {

  private final PaymentsService paymentsService;

  public PaymentsController(PaymentsService paymentsService) {
    this.paymentsService = paymentsService;
  }

  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<IdResponse>> findAllPending(
      @RequestParam(required = false) BigDecimal min,
      @RequestParam(required = false) BigDecimal max
  ) {
    DecimalValidator.maxDecimals("min", min, 2).validate();
    DecimalValidator.maxDecimals("max", max, 2).validate();
    SizeValidator.min("min", min, BigDecimal.ZERO).validate();
    SizeValidator.min("max", max, BigDecimal.ZERO).validate();
    SizeValidator.min("max", max, min, "'max' must be greater than or equal than 'min'").validate();

    min = min == null ? null : min.setScale(2, ROUND_UNNECESSARY);
    max = max == null ? null : max.setScale(2, ROUND_UNNECESSARY);
    return new ResponseEntity<>(paymentsService.findAllPending(min, max), HttpStatus.OK);
  }

  @GetMapping(value = "/cancel_fee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PaymentCancelFeeResponse> getCancellingFee(
      @PathVariable Integer id
  ) {
    return new ResponseEntity<>(paymentsService.getCancellingFee(id), HttpStatus.OK);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Payment> create(
      @RequestBody PaymentRequest request,
      HttpServletRequest http
  ) {
    request.validate();
    Payment payment = request.toDomain(http.getRemoteAddr());
    return new ResponseEntity<>(paymentsService.create(payment), HttpStatus.CREATED);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Payment> cancel(
      @PathVariable("id") int id
  ) {
    return new ResponseEntity<>(paymentsService.cancel(id), HttpStatus.OK);
  }
}
