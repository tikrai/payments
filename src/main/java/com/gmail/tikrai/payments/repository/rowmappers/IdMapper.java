package com.gmail.tikrai.payments.repository.rowmappers;

import com.gmail.tikrai.payments.repository.PaymentsRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class IdMapper implements RowMapper<Integer> {

  @Override
  public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
    return rs.getInt(PaymentsRepository.ID);
  }
}
