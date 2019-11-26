package dev.karolkoltun.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TextCalculatorTest {

    private TextCalculator textCalculator;

    @BeforeEach
    void setup(){
        textCalculator = new TextCalculator();
    }

    static Stream<Arguments> provideInputForAdding(){
        return Stream.of(
                Arguments.of("1+3", 4),
                Arguments.of("3+3", 6),
                Arguments.of("1+9", 10),
                Arguments.of("4+7", 11));
    }

    static Stream<Arguments> provideInputForSubstracting(){
        return Stream.of(
                Arguments.of("4-1", 3),
                Arguments.of("7-6", 1),
                Arguments.of("9-9", 0),
                Arguments.of("5-2", 3));
    }

    @ParameterizedTest
    @MethodSource("provideInputForAdding")
    void shouldAddNumbersAsStrings(String input, double expectedResult){

        double actualResult = textCalculator.calculate(input);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest (name = "{0} should be {1}")
    @MethodSource("provideInputForSubstracting")
    void shouldSubstrackNumbersAsStrings(String input, double expected){

        double actual = textCalculator.calculate(input);

        assertEquals(expected, actual);
    }


}