package com.gmail.tikrai.payments.repository.rowmappers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.repository.PaymentsRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IdMapperTest {

  private final ResultSet rs = mock(ResultSet.class);
  private final IdMapper idMapper = new IdMapper();
  private final Integer id = 5;

  @BeforeEach
  void sutup() throws SQLException {
    when(rs.getInt(PaymentsRepository.ID)).thenReturn(id);
  }

  @Test
  void shouldMapIdRowSuccessfully() throws SQLException {
    Integer actual = idMapper.mapRow(rs, 0);
    assertThat(actual, equalTo(id));
  }

  @AfterEach
  void verifyMocks() throws SQLException {
    verify(rs).getInt(PaymentsRepository.ID);
    verifyNoMoreInteractions(rs);
  }
}
