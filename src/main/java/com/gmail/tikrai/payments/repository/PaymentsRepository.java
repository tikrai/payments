package com.gmail.tikrai.payments.repository;

import com.gmail.tikrai.payments.domain.Payment;
import com.gmail.tikrai.payments.exception.ResourceNotFoundException;
import com.gmail.tikrai.payments.repository.rowmappers.PaymentsMapper;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentsRepository {

  public static final String TABLE = "payments";
  public static final String ID = "id";
  public static final String CREATED = "created";
  public static final String CANCELLED = "cancelled";
  public static final String CANCEL_FEE = "cancel_fee";
  public static final String TYPE = "type";
  public static final String AMOUNT = "amount";
  public static final String CURRENCY = "currency";
  public static final String DEBTOR_IBAN = "debtor_iban";
  public static final String CREDITOR_IBAN = "creditor_iban";
  public static final String BIC_CODE = "bic_code";
  public static final String DETAILS = "details";
  public static final String IP_ADDRESS = "ipaddress";
  public static final String COUNTRY = "country";

  private static PaymentsMapper rowMapper = new PaymentsMapper();

  private final JdbcTemplate db;

  @Autowired
  public PaymentsRepository(JdbcTemplate db) {
    this.db = db;
  }

  public List<Payment> findAll() {
    String sql = String.format("SELECT * FROM %s", TABLE);
    return db.query(sql, rowMapper);
  }

  public List<Payment> findAllPending(BigDecimal min, BigDecimal max) {
    String sql = String.format("SELECT * FROM %s WHERE %s = false", TABLE, CANCELLED);
    if (min != null) {
      sql = String.format("%s AND %s >= %d", sql, AMOUNT, min.unscaledValue());
    }
    if (max != null) {
      sql = String.format("%s AND %s <= %d", sql, AMOUNT, max.unscaledValue());
    }
    return db.query(sql, rowMapper);
  }

  public Optional<Payment> findById(int id) {
    String sql = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE, ID, id);
    return db.query(sql, rowMapper).stream().filter(Objects::nonNull).findFirst();
  }

  public Payment create(Payment payment) {
    String sql = String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) "
            + "VALUES ('%s', '%s', %s, '%s', '%s', '%s', %s, %s, %s) RETURNING %s",
        TABLE,
        CREATED, TYPE, AMOUNT, CURRENCY, DEBTOR_IBAN, CREDITOR_IBAN, BIC_CODE, DETAILS, IP_ADDRESS,
        new Timestamp(payment.created().toEpochMilli()),
        payment.type().toString(),
        payment.amount().unscaledValue(),
        payment.currency().toString(),
        payment.debtorIban(),
        payment.creditorIban(),
        payment.bicCode().map(code -> String.format("'%s'", code)).orElse(null),
        payment.details().map(code -> String.format("'%s'", code)).orElse(null),
        payment.ipAddress().map(code -> String.format("'%s'", code)).orElse(null),
        ID
    );
    RowMapper<Integer> rowMapper = (rs, rowNum) -> rs.getInt(ID);
    int id = db.query(sql, rowMapper).stream().findFirst().orElse(0);
    return payment.withId(id);
  }

  public Payment cancel(int id, BigDecimal fee) {
    String sql = String.format(
        "UPDATE %s SET (%s, %s) = (true, %s) WHERE %s = %s",
        TABLE, CANCELLED, CANCEL_FEE, fee.unscaledValue(), ID, id
    );
    db.update(sql);
    return findById(id).orElseThrow(() -> new ResourceNotFoundException(
        String.format("Cancelled payment with id '%s' was not found", id)
    ));
  }
}
