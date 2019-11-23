package dev.karolkoltun.currency;

import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import static dev.karolkoltun.currency.Currency.*;

/** Serwis walutowy symulujacy dynamiczne zmiany kursow walut. */
public class DynamicCurrencyService implements CurrencyService {

  private Map<Pair<Currency, Currency>, Double> baseCurrencyRates =
      Map.of(
          Pair.of(PLN, EUR), 4.,
          Pair.of(PLN, USD), 3.6,
          Pair.of(USD, EUR), 0.9);

  private boolean dynamicRates = true;
  
  private int counter = 0;

  @Override
  public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
    Optional<Double> foundRate =
        baseCurrencyRates.entrySet().stream()
            .filter(
                e -> e.getKey().equals(Pair.of(from, to)) || e.getKey().equals(Pair.of(to, from)))
            .map(e -> e.getKey().getLeft().equals(from) ? e.getValue() : 1 / e.getValue())
            .findAny();

    if (foundRate.isEmpty()) {
      return null;
    }

    double rate = foundRate.get();

    if (dynamicRates) {
      rate = getDynamicRate(rate);
    }

    return amount.multiply(BigDecimal.valueOf(rate)).setScale(2, RoundingMode.HALF_EVEN);
  }

  void setDynamicRates(boolean on) {
    this.dynamicRates = on;
  }
  
  private double getDynamicRate(double rate) {
    if (counter < 100) {
      ++counter;
    } else {
      counter = 0;
    }

    return rate * 0.9 + counter / 100. * 0.1;
  }
}
