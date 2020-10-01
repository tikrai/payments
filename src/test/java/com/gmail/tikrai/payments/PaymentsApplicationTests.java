package com.gmail.tikrai.payments;

import static org.mockito.Mockito.mock;

import com.gmail.tikrai.payments.config.RestTemplateConfiguration;
import com.gmail.tikrai.payments.database.DbInitListener;
import com.gmail.tikrai.payments.util.RestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

class PaymentsApplicationTests {

  private final RestTemplate restTemplate = new RestTemplateConfiguration().restTemplate();
  private final RestUtil restUtil = new RestUtil();
  private final RestUtil.Endpoint endpoint = new RestUtil.Endpoint();
  private final JdbcTemplate db = mock(JdbcTemplate.class);
  private final DbInitListener dbInitListener = new DbInitListener(db);

  @Test
  void contextLoads() {
    dbInitListener.onApplicationEvent(null);
  }
}
