package com.gmail.tikrai.payments.utils;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Optional;

public class Matchers {

  public static <T> org.hamcrest.Matcher<Optional<T>> isOptionalOf(T operand) {
    return equalTo(Optional.ofNullable(operand));
  }
}
