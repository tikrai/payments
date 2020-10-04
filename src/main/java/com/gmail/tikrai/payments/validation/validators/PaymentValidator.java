package com.gmail.tikrai.payments.validation.validators;

import com.gmail.tikrai.payments.request.PaymentRequest;
import com.gmail.tikrai.payments.validation.Validator;
import java.util.Optional;

public class PaymentValidator implements Validator {

  private static final String TYPE1_CURRENCY_MESSAGE = "'currency' must be 'EUR' for TYPE1 payment";
  private static final String TYPE1_BIC_MESSAGE =      "'bic_code' not available for TYPE1 payment";
  private static final String TYPE1_DETAILS_MESSAGE =  "'details' cannot be null for TYPE1 payment";
  private static final String TYPE2_BIC_MESSAGE =      "'bic_code' not available for TYPE2 payment";
  private static final String TYPE2_CURRENCY_MESSAGE = "'currency' must be 'USD' for TYPE2 payment";
  private static final String TYPE3_BIC_MESSAGE =     "'bic_code' cannot be null for TYPE3 payment";
  private static final String TYPE3_DETAILS_MESSAGE =  "'details' not available for TYPE3 payment";
  private final String message;

  private PaymentValidator(String message) {
    this.message = message;
  }

  public static PaymentValidator ofType1(PaymentRequest value) {

    if (!value.type().equals("TYPE1")) {
      return new PaymentValidator(null);
    }

    if (!value.currency().equals("EUR")) {
      return new PaymentValidator(TYPE1_CURRENCY_MESSAGE);
    }

    if (value.bicCode() != null) {
      return new PaymentValidator(TYPE1_BIC_MESSAGE);
    }

    return new PaymentValidator(value.details() == null ? TYPE1_DETAILS_MESSAGE : null);
  }


  public static PaymentValidator ofType2(PaymentRequest value) {

    if (!value.type().equals("TYPE2")) {
      return new PaymentValidator(null);
    }

    if (!value.currency().equals("USD")) {
      return new PaymentValidator(TYPE2_CURRENCY_MESSAGE);
    }

    if (value.bicCode() != null) {
      return new PaymentValidator(TYPE2_BIC_MESSAGE);
    }

    return new PaymentValidator(null);
  }


  public static PaymentValidator ofType3(PaymentRequest value) {

    if (!value.type().equals("TYPE3")) {
      return new PaymentValidator(null);
    }

    if (value.details() != null) {
      return new PaymentValidator(TYPE3_DETAILS_MESSAGE);
    }

    return new PaymentValidator(value.bicCode() == null ? TYPE3_BIC_MESSAGE : null);
  }

  @Override
  public Optional<String> valid() {
    return Optional.ofNullable(message);
  }
}
