package com.gmail.tikrai.payments.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ResourceNotFoundException;
import com.gmail.tikrai.payments.fixture.Fixture;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

class PaymentsRepositoryTest {
  private final JdbcTemplate db = mock(JdbcTemplate.class);
  private final PaymentsRepository paymentsRepository = new PaymentsRepository(db);

  private final Payment payment = Fixture.payment().build();
  private final List<Payment> paymentList = Collections.singletonList(payment);

  @Test
  void shouldFindAllPayments() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    List<Payment> actual = paymentsRepository.findAll();

    assertThat(actual, equalTo(Collections.singletonList(payment)));
    String expectedQuery = "SELECT * FROM payments";
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
    verifyNoMoreInteractions(db);
  }

  @Test
  void shouldFindAllPendingPayments() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    List<Payment> actual = paymentsRepository.findAllPending();

    assertThat(actual, equalTo(Collections.singletonList(payment)));
    String expectedQuery = "SELECT * FROM payments WHERE cancelled = false";
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
    verifyNoMoreInteractions(db);
  }

  @Test
  void shouldFindPaymentById() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    Optional<Payment> actual = paymentsRepository.findById(payment.id());

    assertThat(actual, equalTo(Optional.of(payment)));
    String expectedQuery = String.format("SELECT * FROM payments WHERE id = '%s'", payment.id());
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
    verifyNoMoreInteractions(db);
  }

  @Test
  void shouldCreatePayment() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(1));

    Payment actual = paymentsRepository.create(payment);

    assertThat(actual, equalTo(payment.withId(1)));
    String expectedQuery = String.format(
        "INSERT INTO payments "
            + "(created, type, amount, currency, debtor_iban, creditor_iban, bic_code, details) "
            + "VALUES "
            + "('%s', 'TYPE1', 1001, 'EUR', 'LT0001', 'LT9999', 'AGBLLT2X', 'details') "
            + "RETURNING id",
        new Timestamp(payment.created().toEpochMilli()));
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
    verifyNoMoreInteractions(db);
  }

  @Test
  void shouldCancelPayment() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    Payment actual = paymentsRepository.cancel(payment.id());

    assertThat(actual, equalTo(payment));
    String expectedQuery = String
        .format("UPDATE payments SET (cancelled) = (true) WHERE id = %s", payment.id());
    verify(db).update(eq(expectedQuery));
    String expectedQuery2 = String.format("SELECT * FROM payments WHERE id = '%s'", payment.id());
    verify(db).query(eq(expectedQuery2), any(RowMapper.class));
    verifyNoMoreInteractions(db);
  }

  @Test
  void shouldFailFindingPaymentAfterCancel() {
    String message = assertThrows(
        ResourceNotFoundException.class,
        () -> paymentsRepository.cancel(payment.id())
    ).getMessage();


    String expectedMessage = String
        .format("Cancelled payment with id '%s' was not found", payment.id());
    assertThat(message, equalTo(expectedMessage));

    String expectedQuery = String
        .format("UPDATE payments SET (cancelled) = (true) WHERE id = %s", payment.id());
    verify(db).update(eq(expectedQuery));
    String expectedQuery2 = String.format("SELECT * FROM payments WHERE id = '%s'", payment.id());
    verify(db).query(eq(expectedQuery2), any(RowMapper.class));
    verifyNoMoreInteractions(db);
  }
}
