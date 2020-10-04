package com.gmail.tikrai.payments.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.CancelFee;
import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

class PaymentsRepositoryTest {
  private final JdbcTemplate db = mock(JdbcTemplate.class);
  private final PaymentsRepository paymentsRepository = new PaymentsRepository(db);

  private final Payment payment = Fixture.payment().build();
  private final List<Payment> paymentList = Collections.singletonList(payment);
  private final BigDecimal zero = BigDecimal.valueOf(0, 2);
  private final CancelFee fee = new CancelFee(payment.id(), true, zero, Instant.now());

  @Test
  void shouldFindAllPayments() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    List<Payment> actual = paymentsRepository.findAll();

    assertThat(actual, equalTo(Collections.singletonList(payment)));
    String expectedQuery = "SELECT * FROM payments";
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
  }

  @Test
  void shouldFindAllPendingPayments() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    List<Payment> actual = paymentsRepository.findAllPending(null, null);

    assertThat(actual, equalTo(Collections.singletonList(payment)));
    String expectedQuery = "SELECT * FROM payments WHERE cancelled IS NULL";
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
  }

  @Test
  void shouldFindFilteredPendingPayments() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    List<Payment> actual = paymentsRepository
        .findAllPending(BigDecimal.valueOf(0.01), BigDecimal.valueOf(2.09));

    assertThat(actual, equalTo(Collections.singletonList(payment)));
    String expectedQuery =
        "SELECT * FROM payments WHERE cancelled IS NULL AND amount >= 1 AND amount <= 209";
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
  }

  @Test
  void shouldFindPaymentById() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    Optional<Payment> actual = paymentsRepository.findNonCancelledById(payment.id());

    assertThat(actual, equalTo(Optional.of(payment)));
    String expectedQuery = String.format("SELECT payments.*, cancel_coeff.coeff "
        + "FROM payments LEFT JOIN cancel_coeff ON payments.type = cancel_coeff.type "
        + "WHERE id = '%s' AND cancelled IS NULL",
        payment.id());
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
  }

  @Test
  void shouldCreatePayment() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(1));

    Payment actual = paymentsRepository.create(payment);

    assertThat(actual, equalTo(payment.withId(1)));
    String expectedQuery = String.format(
        "INSERT INTO payments "
            + "(created, type, amount, currency, debtor_iban, creditor_iban, "
            + "bic_code, details, ipaddress) "
            + "VALUES "
            + "('%s', 'TYPE1', 1001, 'EUR', 'LT0001', 'LT9999', "
            + "'AGBLLT2X', 'details', '127.0.0.1') "
            + "RETURNING id",
        new Timestamp(payment.created().toEpochMilli()));
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
  }

  @Test
  void shouldCreatePaymentWithoutIpAddress() {
    Payment paymentNoIp = Fixture.payment().of(payment).ipAddress(null).build();
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(1));

    Payment actual = paymentsRepository.create(paymentNoIp);

    assertThat(actual, equalTo(paymentNoIp.withId(1)));
    String expectedQuery = String.format(
        "INSERT INTO payments "
            + "(created, type, amount, currency, debtor_iban, creditor_iban, "
            + "bic_code, details, ipaddress) "
            + "VALUES "
            + "('%s', 'TYPE1', 1001, 'EUR', 'LT0001', 'LT9999', "
            + "'AGBLLT2X', 'details', null) "
            + "RETURNING id",
        new Timestamp(paymentNoIp.created().toEpochMilli()));
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
  }

  @Test
  void shouldLogNotified() {
    boolean success = true;
    paymentsRepository.logNotified(payment.id(), success);

    String expectedQuery = String
        .format("UPDATE payments SET (notified) = ('%s') WHERE id = %s", success, payment.id());
    verify(db).update(eq(expectedQuery));
  }

  @Test
  void shouldCancelPayment() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(paymentList);

    Optional<Payment> actual = paymentsRepository.cancel(fee);

    assertThat(actual, equalTo(Optional.of(payment)));
    String expectedQuery = String.format(
        "UPDATE payments SET (cancelled, cancel_fee) = ('%s', 0) WHERE id = %s RETURNING *",
        new Timestamp(fee.time().toEpochMilli()), payment.id()
    );
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
  }

  @Test
  void shouldFailtoCancelPaymentIfNotExists() {
    when(db.query(anyString(), any(RowMapper.class))).thenReturn(Collections.emptyList());

    Optional<Payment> actual = paymentsRepository.cancel(fee);

    assertThat(actual, equalTo(Optional.empty()));
    String expectedQuery = String.format(
        "UPDATE payments SET (cancelled, cancel_fee) = ('%s', 0) WHERE id = %s RETURNING *",
        new Timestamp(fee.time().toEpochMilli()), payment.id()
    );
    verify(db).query(eq(expectedQuery), any(RowMapper.class));
  }

  @AfterEach
  void verifyMocks() {
    verifyNoMoreInteractions(db);
  }
}
