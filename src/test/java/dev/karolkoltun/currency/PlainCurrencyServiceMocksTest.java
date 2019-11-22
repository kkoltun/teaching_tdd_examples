package dev.karolkoltun.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static dev.karolkoltun.currency.Currency.EUR;
import static dev.karolkoltun.currency.Currency.PLN;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PlainCurrencyServiceMocksTest {

  private CurrencyService currencyService;

  @BeforeEach
  void setup() {
    currencyService = new PlainCurrencyService();
  }

  @Test
  void mockShouldWork() {
    // Given
    currencyService = Mockito.mock(CurrencyService.class);
    when(currencyService.convert(valueOf(2), EUR, PLN)).thenReturn(valueOf(3));

    // When
    BigDecimal firstAmount = currencyService.convert(valueOf(2), EUR, PLN);

    // Then
    assertEquals(firstAmount, valueOf(3));
  }
}
