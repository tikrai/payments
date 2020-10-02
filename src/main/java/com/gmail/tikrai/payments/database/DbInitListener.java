package com.gmail.tikrai.payments.database;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbInitListener implements ApplicationListener<ContextRefreshedEvent> {

  private final JdbcTemplate db;

  @Autowired
  public DbInitListener(JdbcTemplate db) {
    this.db = db;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    Arrays.asList(
        "CREATE TABLE IF NOT EXISTS payments("
            + "  id            SERIAL PRIMARY KEY,"
            + "  created       TIMESTAMP NOT NULL,"
            + "  cancelled     BOOLEAN NOT NULL DEFAULT FALSE,"
            + "  cancel_fee    INTEGER,"
            + "  type          CHAR(5) NOT NULL,"
            + "  amount        INTEGER NOT NULL,"
            + "  currency      CHAR(3) NOT NULL,"
            + "  debtor_iban   VARCHAR(20) NOT NULL,"
            + "  creditor_iban VARCHAR(20) NOT NULL,"
            + "  bic_code      VARCHAR(20),"
            + "  details       VARCHAR(255),"
            + "  ipAddress     VARCHAR(255),"
            + "  country       VARCHAR(50)"
            + ");",
        "CREATE TABLE IF NOT EXISTS cancel_coeff("
            + "  type          CHAR(5) PRIMARY KEY,"
            + "  coeff         INTEGER"
            + ");",
        "INSERT INTO "
            + "cancel_coeff (type, coeff) "
            + "VALUES "
            + "('TYPE1', 5), ('TYPE2', 10), ('TYPE3', 15) "
            + "ON CONFLICT DO NOTHING"
    ).forEach(db::update);
  }
}
