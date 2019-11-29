package dev.karolkoltun.calculator;

import dev.karolkoltun.exceptions.DivisionByZeroException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleCalculatorTest {

  SimpleCalculator simpleCalculator;

  @BeforeEach
  void setup() {
    simpleCalculator = new SimpleCalculator();
  }


  @ParameterizedTest
  @CsvSource({"3,6,9", "9,12,21", "11,5,16"})
  void shouldAddNumbers(int FirstNumber, int SecondNumber, int expectedResult) {
    //When
    int actualResult = simpleCalculator.add(FirstNumber, SecondNumber);

    //Then
    assertEquals(expectedResult, actualResult);
  }

  @ParameterizedTest
  @CsvSource({"10,4,6", "9,9,0", "19,4,15"})
  void shouldSubtractNumbers(int first, int second, int expectedResult) {
    //When
    int actualResult = simpleCalculator.subtract(first, second);

    //Then
    assertEquals(expectedResult, actualResult);
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/data.csv", numLinesToSkip = 0)
  void shouldMultiplyNumbers(int first, int second, int expectedResult) {
    //When
    int actualResult = simpleCalculator.multiply(first, second);

    //Then
    assertEquals(expectedResult, actualResult);
  }

  @ParameterizedTest
  @CsvSource({"0,0", "4,3", "7,13"})
  void shouldComputeFibonacci(int index, int expectedNumber) {
    // When
    int actualNumber = simpleCalculator.calculateFibonacci(index);

    // Then
    assertEquals(expectedNumber, actualNumber);
  }

  @Test
  void shouldDivideNumbers() throws DivisionByZeroException {
    //Given
    double firstNumber = 15;
    double secondNumber = 3;
    double expectedNumber = 5;

    //When
    double actualNumber = simpleCalculator.divide(firstNumber, secondNumber);

    //Then
    assertEquals(expectedNumber, actualNumber);
  }

  @Test
  void shouldThrowException(){
    // Given
    double first = 4;
    double second = 0;
    String expectedPhrase = "4.0/0.0 is illegal.";

    Executable divideByZero = () -> simpleCalculator.divide(first, second);

    DivisionByZeroException ex = assertThrows(DivisionByZeroException.class, divideByZero);
    String actualPhrase = ex.getPhrase();
    assertEquals(actualPhrase, expectedPhrase);
  }

  @ParameterizedTest
  @ValueSource(ints = {3,7,9,11,15})
  void shouldSayIsOdd(int number){
    assertTrue(simpleCalculator.isOdd(number));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ad","wer","dafs"})
  void shouldSayItsNotNumber(String str){
    assertFalse(simpleCalculator.isNumber(str));
  }

  @Test
  void shouldGetCorrectDivisors() {
    //Given
    int number = 12;
    Integer[] expectedTab = {1,2,3,4,6,12};

    //When
    List<Integer> listOfDivisors = simpleCalculator.getDivisors(number);

     //Then
    assertEquals(6, listOfDivisors.size());
    assertTrue(listOfDivisors.containsAll(Arrays.asList(1,2,3,4,6,12)));
  }

  @ParameterizedTest (name = "{0} with tax: {1} is : {2}")
  @CsvSource({"2346,18,2768.28", "4112,20,4934.4", "1766,13,1995.58", "418,21,505.78"})
  void shouldComputeGrossAmount(double x, double y, double result){
    //When
    BigDecimal actualAmount = simpleCalculator
            .calculateGrossAmount(BigDecimal.valueOf(x), BigDecimal.valueOf(y));

    //Then
    assertThat(actualAmount).isEqualByComparingTo(BigDecimal.valueOf(result));
  }

}
