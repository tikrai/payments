package com.gmail.tikrai.payments.repository;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentsRepository {

  private static final String TABLE = "payments";
  private static final String ID = "id";
  private static final String CANCELLED = "cancelled";
  private static final String TYPE = "type";
  private static final String AMOUNT = "amount";
  private static final String CURRENCY = "currency";
  private static final String DEBTOR_IBAN = "debtor_iban";
  private static final String CREDITOR_IBAN = "creditor_iban";
  private static final String BIC_CODE = "bic_code";
  private static final String DETAILS = "details";
  private static final String IP_ADDRESS = "ipAddress";
  private static final String COUNTRY = "country";

  private static RowMapper<Payment> rowMapper = (rs, rowNum) -> new Payment(
      rs.getInt(ID),
      rs.getBoolean(CANCELLED),
      Payment.Type.valueOf(rs.getString(TYPE)),
      BigDecimal.valueOf(rs.getInt(AMOUNT), 2),
      Payment.Currency.valueOf(rs.getString(CURRENCY)),
      rs.getString(DEBTOR_IBAN),
      rs.getString(CREDITOR_IBAN),
      rs.getString(BIC_CODE),
      rs.getString(DETAILS),
      rs.getString(IP_ADDRESS),
      rs.getString(COUNTRY)
  );

  private final JdbcTemplate db;

  @Autowired
  public PaymentsRepository(JdbcTemplate db) {
    this.db = db;
  }

  public List<Payment> findAllPending() {
    String sql = String.format("SELECT * FROM %s WHERE %s = false", TABLE, CANCELLED);
    return db.query(sql, rowMapper);
  }

  public Optional<Payment> findById(int id) {
    String sql = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE, ID, id);
    return db.query(sql, rowMapper).stream().filter(Objects::nonNull).findFirst();
  }

  public Payment create(Payment payment) {
    String sql = String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES ('%s', %s, '%s', '%s', '%s', %s, %s) RETURNING %s",
        TABLE, TYPE, AMOUNT, CURRENCY, DEBTOR_IBAN, CREDITOR_IBAN, BIC_CODE, DETAILS,
        payment.type().toString(),
        payment.amount().unscaledValue(),
        payment.currency().toString(),
        payment.debtorIban(),
        payment.creditorIban(),
        payment.bicCode().map(code -> String.format("'%s'", code)).orElse(null),
        payment.details().map(code -> String.format("'%s'", code)).orElse(null),
        ID
    );
    int id = db.query(sql, (rs, rowNum) -> rs.getInt(ID)).stream().findFirst().orElse(0);
    return payment.withId(id);
  }

  public Payment cancel(int id) {
    String sql = String.format(
        "UPDATE %s SET (%s) = (true) WHERE %s = %s", TABLE, CANCELLED, ID, id
    );
    db.update(sql);
    return findById(id).orElseThrow(() -> new ResourceNotFoundException(
        String.format("Cancelled payment with id '%s' was not found", id)
    ));
  }
}
