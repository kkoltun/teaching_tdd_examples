package dev.karolkoltun.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static dev.karolkoltun.currency.Currency.EUR;
import static dev.karolkoltun.currency.Currency.PLN;
import static java.math.BigDecimal.valueOf;

/** Bardzo ograniczony serwis do przeliczania kwot na inne waluty. */
public class PlainCurrencyService implements CurrencyService {

  private static int PLN_TO_EUR_RATE = 4;

  @Override
  public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
    if (from.equals(to)) {
      return amount;
    }

    if (from.equals(PLN) && to.equals(EUR)) {
      return amount.divide(valueOf(PLN_TO_EUR_RATE), RoundingMode.HALF_EVEN);
    } else if (from.equals(EUR) && to.equals(PLN)) {
      return amount.multiply(valueOf(PLN_TO_EUR_RATE, 1));
    } else {
      // This is not nice...
      return null;
    }
  }
}
