package dev.karolkoltun.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleCalculatorTest {

  SimpleCalculator simpleCalculator;

  @BeforeEach
  void setup() {
    simpleCalculator = new SimpleCalculator();
  }

  @Test
  void simplestTest() {
    // Given
    boolean expectedValue = true;

    // When
    boolean actualValue = true;

    // Then
    assertEquals(expectedValue, actualValue);
  }
}
