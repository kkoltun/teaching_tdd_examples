package dev.karolkoltun.currency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class CurrencyServiceMockTest {

    private CurrencyService currencyService;

    @BeforeEach
    void setupMock() {
        currencyService = Mockito.mock(CurrencyService.class);
    }

    @Test
    void testMock1() {
        // Configure Mock
        when(currencyService.convert(
                BigDecimal.valueOf(100),
                Currency.EUR,
                Currency.PLN)).thenReturn(BigDecimal.valueOf(140));

        // Test Mock
        BigDecimal result = currencyService.convert(BigDecimal.valueOf(100),
                Currency.EUR,
                Currency.PLN);

        assertEquals(BigDecimal.valueOf(140), result);
    }

    @Test
    void testMock2() {
        // Configure Mock
        when(currencyService.convert(
                BigDecimal.valueOf(100),
                Currency.PLN,
                Currency.EUR)).thenReturn(BigDecimal.valueOf(400));

        // Test Mock
        BigDecimal actualResult = currencyService.convert(
                BigDecimal.valueOf(100),
                Currency.PLN,
                Currency.EUR);

        assertEquals(BigDecimal.valueOf(400), actualResult);
    }

    @Test
    void testMock3() {
        // Configure Mock
        when(currencyService.convert(
                any(BigDecimal.class),
                any(Currency.class),
                any(Currency.class))).thenReturn(BigDecimal.valueOf(150));

        // Test Mock
        BigDecimal actualResult = currencyService.convert(
                BigDecimal.valueOf(300),
                Currency.EUR,
                Currency.USD);

        assertEquals(BigDecimal.valueOf(150), actualResult);
    }

    @Test
    void testMock4() {
        //Configure Mock
        when(currencyService.convert(
                eq(BigDecimal.valueOf(100)),
                any(Currency.class),
                any(Currency.class))).thenReturn(BigDecimal.valueOf(200));

        // Test
        BigDecimal actualResult = currencyService.convert(
                BigDecimal.valueOf(100),
                Currency.EUR,
                Currency.PLN);

        assertEquals(BigDecimal.valueOf(200), actualResult);
    }

    @Test
    void testMock5Exception(){
        // Configure
        when(currencyService.convert(
                any(BigDecimal.class),
                eq(Currency.PLN),
                any(Currency.class))).thenThrow(new RuntimeException("Currency not found."));

        //Test
        Executable actualResult = () -> currencyService.convert(BigDecimal.valueOf(344), Currency.PLN, Currency.EUR);

        assertThrows(RuntimeException.class, actualResult);

    }


}
