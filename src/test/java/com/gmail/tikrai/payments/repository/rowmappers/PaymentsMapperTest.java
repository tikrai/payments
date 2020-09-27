package com.gmail.tikrai.payments.repository.rowmappers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.fixture.Fixture;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentsMapperTest {

  private final ResultSet rs = mock(ResultSet.class);
  private final PaymentsMapper paymentsMapper = new PaymentsMapper();
  private Payment payment = Fixture.payment().cancelFee(0).build();

  @BeforeEach
  void sutup() throws SQLException {
    when(rs.getInt(PaymentsRepository.ID)).thenReturn(payment.id());
    when(rs.getTimestamp(PaymentsRepository.CREATED))
        .thenReturn(new Timestamp(payment.created().toEpochMilli()));
    when(rs.getBoolean(PaymentsRepository.CANCELLED)).thenReturn(payment.cancelled());
    when(rs.getInt(PaymentsRepository.CANCEL_FEE))
        .thenReturn(payment.cancelFee().unscaledValue().intValue());
    when(rs.getString(PaymentsRepository.TYPE)).thenReturn(payment.type().toString());
    when(rs.getInt(PaymentsRepository.AMOUNT))
        .thenReturn(payment.amount().unscaledValue().intValue());
    when(rs.getString(PaymentsRepository.CURRENCY)).thenReturn(payment.currency().toString());
    when(rs.getString(PaymentsRepository.DEBTOR_IBAN)).thenReturn(payment.debtorIban());
    when(rs.getString(PaymentsRepository.CREDITOR_IBAN)).thenReturn(payment.creditorIban());
    when(rs.getString(PaymentsRepository.BIC_CODE)).thenReturn(payment.bicCode().orElse(null));
    when(rs.getString(PaymentsRepository.DETAILS)).thenReturn(payment.details().orElse(null));
    when(rs.getString(PaymentsRepository.IP_ADDRESS)).thenReturn(payment.ipAddress().orElse(null));
    when(rs.getString(PaymentsRepository.COUNTRY)).thenReturn(payment.country().orElse(null));
  }

  @Test
  void shouldMapBookRowSuccessfully() throws SQLException {
    when(rs.wasNull()).thenReturn(false);
    Payment actual = paymentsMapper.mapRow(rs, 0);
    assertThat(actual, equalTo(payment));
  }

  @Test
  void shouldMapBookRowSuccessfullyWithNullCancelFee() throws SQLException {
    when(rs.wasNull()).thenReturn(true);
    Payment actual = paymentsMapper.mapRow(rs, 0);
    assertThat(actual, equalTo(payment.withCancelFee(null)));
  }

  @AfterEach
  void verifyMocks() throws SQLException {
    verify(rs).getInt(PaymentsRepository.ID);
    verify(rs).getTimestamp(PaymentsRepository.CREATED);
    verify(rs).getBoolean(PaymentsRepository.CANCELLED);
    verify(rs).getInt(PaymentsRepository.CANCEL_FEE);
    verify(rs).getString(PaymentsRepository.TYPE);
    verify(rs).getInt(PaymentsRepository.AMOUNT);
    verify(rs).getString(PaymentsRepository.CURRENCY);
    verify(rs).getString(PaymentsRepository.DEBTOR_IBAN);
    verify(rs).getString(PaymentsRepository.CREDITOR_IBAN);
    verify(rs).getString(PaymentsRepository.BIC_CODE);
    verify(rs).getString(PaymentsRepository.DETAILS);
    verify(rs).getString(PaymentsRepository.IP_ADDRESS);
    verify(rs).getString(PaymentsRepository.COUNTRY);
    verify(rs).wasNull();
    verifyNoMoreInteractions(rs);
  }
}
