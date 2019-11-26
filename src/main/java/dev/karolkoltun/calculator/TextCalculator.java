package dev.karolkoltun.calculator;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

/** Proste, ryzykowne, lekko bledne i ograniczone podejscie do kalkulatora bazujacego na tekscie. */
class TextCalculator {

  /**
   * Oblicza podane wyrazenie. Zaklada ze wyrazenie sklada sie z jednej cyfry, znaku i drugiej
   * cyfry. Tylko znak "+" jest wspierany. Przykladowe wspierane wyrazenie: "5+4".
   *
   * @param phrase wyrazenie do obliczenia
   * @return wynik
   */
  double calculate(String phrase) {

    int firstNumber = Character.getNumericValue(phrase.charAt(0));
    for (int i = 1; i < phrase.length(); i++) {
      String subPhrase = phrase.substring(0, i);
      if (!isNumber(subPhrase)) {
        firstNumber = Integer.parseInt(subPhrase);
      }
    }

      char symbol = phrase.charAt(1);

      int secondNumber = Character.getNumericValue(phrase.charAt(2));

      if (symbol == '+') {
        return firstNumber + secondNumber;
      } else if (symbol == '-') {
        return firstNumber - secondNumber;
      } else {
        throw new RuntimeException("Symbol " + symbol + " is not supported!");
      }
    }

    boolean isNumber (String text){
      return NumberUtils.isCreatable(text);
    }

  }

