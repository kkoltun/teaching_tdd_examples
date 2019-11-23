package dev.karolkoltun.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static dev.karolkoltun.currency.Currency.*;
import static org.assertj.core.api.Assertions.assertThat;

class DynamicCurrencyServiceTest {
  
  static Stream<Arguments> getTestData() {
    return Stream.of(
        Arguments.of(BigDecimal.valueOf(100.00), EUR, BigDecimal.valueOf(25.00), PLN),
        Arguments.of(BigDecimal.valueOf(130.10), PLN, BigDecimal.valueOf(520.40), EUR),
        Arguments.of(BigDecimal.valueOf(100.00), PLN, BigDecimal.valueOf(360.00), USD),
        Arguments.of(BigDecimal.valueOf(120.00), USD, BigDecimal.valueOf(33.33), PLN),
        Arguments.of(BigDecimal.valueOf(90.00), USD, BigDecimal.valueOf(81.00), EUR),
        Arguments.of(BigDecimal.valueOf(32.00), EUR, BigDecimal.valueOf(35.56), USD)
    );
  }
  
  private DynamicCurrencyService currencyService;

  @BeforeEach
  void setup() {
    currencyService = new DynamicCurrencyService();
  }

  @ParameterizedTest(name = "{0} {1} converts to {2} {3}")
  @MethodSource("getTestData")
  void shouldConvertCurrenciesInStaticMode(BigDecimal fromAmount, Currency fromCurrency, BigDecimal expectedAmount, Currency toCurrency) {
    // Given
    currencyService.setDynamicRates(false);
    
    // When
    BigDecimal actualAmount = currencyService.convert(fromAmount, fromCurrency, toCurrency);
    
    // Then
    assertThat(actualAmount).isEqualByComparingTo(expectedAmount);
  }
  
  @Test
  void shouldNeverReturnTwoSameValuesInDynamicMode() {
    // Given
    currencyService.setDynamicRates(true);
    BigDecimal fromAmount = BigDecimal.valueOf(100);
    Currency fromCurrency = EUR;
    BigDecimal nonDynamicResultAmount = BigDecimal.valueOf(400);
    Currency toCurrency = PLN;
    
    // When
    BigDecimal actualAmount1 = currencyService.convert(fromAmount, fromCurrency, toCurrency);
    BigDecimal actualAmount2 = currencyService.convert(fromAmount, fromCurrency, toCurrency);
    
    // Then
    assertThat(actualAmount1).isNotEqualByComparingTo(nonDynamicResultAmount);
    assertThat(actualAmount2).isNotEqualByComparingTo(nonDynamicResultAmount);
    assertThat(actualAmount1).isNotEqualByComparingTo(actualAmount2);
  }
}