package dev.karolkoltun.calculator;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

    String phraseWithoutSpaces = phrase.replaceAll(" ", "");
    int index = phraseWithoutSpaces.length();

    while (!isNumber(phraseWithoutSpaces.substring(0, index))) {
      index--;
    }

    String firstPhrase = phraseWithoutSpaces.substring(0, index);
    String secondPhrase = phraseWithoutSpaces.substring(index + 1);
    double firstNumber = Double.parseDouble(firstPhrase);
    double secondNumber = Double.parseDouble(secondPhrase);
    char symbol = phraseWithoutSpaces.charAt(index);

    switch (symbol) {
      case '+':
        return firstNumber + secondNumber;
      case '-':
        return firstNumber - secondNumber;
      case '*':
        return firstNumber * secondNumber;
      case '/':
        return firstNumber / secondNumber;
      default:
        throw new RuntimeException("Symbol " + symbol + " is not supported!");
    }
  }

    boolean isNumber (String text){
      return NumberUtils.isCreatable(text);
    }

}

