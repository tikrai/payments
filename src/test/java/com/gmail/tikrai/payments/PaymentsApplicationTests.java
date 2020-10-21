package com.gmail.tikrai.payments;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.tikrai.payments.config.DocumentationViewConfiguration;
import com.gmail.tikrai.payments.config.RestTemplateConfiguration;
import com.gmail.tikrai.payments.database.DbInitListener;
import com.gmail.tikrai.payments.util.RestUtil;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

class PaymentsApplicationTests {

  private final RestTemplate restTemplate = new RestTemplateConfiguration().restTemplate();

  @Test
  void contextLoads() throws IOException {

    //covers RestTemplateConfiguration
    ViewControllerRegistry reg = mock(ViewControllerRegistry.class);
    ViewControllerRegistration regn = mock(ViewControllerRegistration.class);
    when(reg.addViewController(anyString())).thenReturn(regn);
    new DocumentationViewConfiguration().addViewControllers(reg);

    //covers RestTemplateConfiguration
    ClientHttpResponse resp = mock(ClientHttpResponse.class);
    restTemplate.getErrorHandler().hasError(resp);
    restTemplate.getErrorHandler().handleError(resp);

    //covers DbInitListener
    JdbcTemplate db = mock(JdbcTemplate.class);
    new DbInitListener(db).onApplicationEvent(null);
    verify(db, times(3)).update(anyString());

    //covers RestUtil
    new RestUtil();
    new RestUtil.Endpoint();
  }
}
