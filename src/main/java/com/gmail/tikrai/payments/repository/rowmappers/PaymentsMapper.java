package com.gmail.tikrai.payments.repository.rowmappers;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.repository.PaymentsRepository;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;

public class PaymentsMapper implements RowMapper<Payment> {

  @Override
  public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Payment(
        rs.getInt(PaymentsRepository.ID),
        getInstant(rs, PaymentsRepository.CREATED),
        getInstant(rs, PaymentsRepository.CANCELLED),
        getBigDecimal(rs, PaymentsRepository.CANCEL_FEE),
        Payment.Type.valueOf(rs.getString(PaymentsRepository.TYPE)),
        BigDecimal.valueOf(rs.getInt(PaymentsRepository.AMOUNT), 2),
        Payment.Currency.valueOf(rs.getString(PaymentsRepository.CURRENCY)),
        rs.getString(PaymentsRepository.DEBTOR_IBAN),
        rs.getString(PaymentsRepository.CREDITOR_IBAN),
        rs.getString(PaymentsRepository.BIC_CODE),
        rs.getString(PaymentsRepository.DETAILS),
        getCancelCoeff(rs, PaymentsRepository.COEFF),
        rs.getString(PaymentsRepository.IP_ADDRESS),
        getBoolean(rs, PaymentsRepository.NOTIFIED)
    );
  }

  private Instant getInstant(ResultSet rs, String cancelled) throws SQLException {
    return Optional.ofNullable(rs.getTimestamp(cancelled)).map(Timestamp::toInstant).orElse(null);
  }

  private BigDecimal getBigDecimal(ResultSet rs, String colName) throws SQLException {
    BigDecimal value = BigDecimal.valueOf(rs.getInt(colName), 2);
    return rs.wasNull() ? null : value;
  }

  private Integer getCancelCoeff(ResultSet rs, String colName) {
    try {
      return rs.getInt(colName);
    } catch (SQLException e) {
      return null;
    }
  }

  private Boolean getBoolean(ResultSet rs, String colName) throws SQLException {
    boolean value = rs.getBoolean(colName);
    return rs.wasNull() ? null : value;
  }
}
