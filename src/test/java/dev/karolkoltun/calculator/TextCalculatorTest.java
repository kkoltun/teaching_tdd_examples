package dev.karolkoltun.calculator;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
                Arguments.of("50 0+3", 503),
                Arguments.of("32+3", 35),
                Arguments.of("1+9", 10),
                Arguments.of("14+27", 41));
    }

    static Stream<Arguments> provideInputForSubtracting(){
        return Stream.of(
                Arguments.of("4-1", 3),
                Arguments.of("74-26 ", 48),
                Arguments.of("1 9-9", 10),
                Arguments.of("1  23-10", 113));
    }

    static Stream<Arguments> inputForMultiplying(){
        return Stream.of(
                Arguments.of("4*4", 16),
                Arguments.of(" 10.5*3", 31.5),
                Arguments.of("12*12", 144),
                Arguments.of("9*1 20", 1080));
    }

    static Stream<Arguments> inputForDivision(){
        return Stream.of(
                Arguments.of("8/2", 4),
                Arguments.of("21/ 3", 7),
                Arguments.of("50/20", 2.5),
                Arguments.of("20 0/4", 50));
    }

    @ParameterizedTest
    @MethodSource("provideInputForAdding")
    void shouldAddNumbersAsStrings(String input, double expectedResult){

        double actualResult = textCalculator.calculate(input);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest (name = "{0} should be {1}")
    @MethodSource("provideInputForSubtracting")
    void shouldSubtractNumbersAsStrings(String input, double expected){

        double actual = textCalculator.calculate(input);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("inputForMultiplying")
    void shouldMultiplyNumbers(String phrase, double result){

        double actual = textCalculator.calculate(phrase);

        assertEquals(result, actual);
    }

    @ParameterizedTest
    @MethodSource("inputForDivision")
    void shouldDivideNumbers(String phrase, double result){

        double actual = textCalculator.calculate(phrase);

        assertEquals(result, actual);
    }
}