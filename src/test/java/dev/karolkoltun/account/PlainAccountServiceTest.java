package dev.karolkoltun.account;

import dev.karolkoltun.exceptions.InvalidPayment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PlainAccountServiceTest {

    private PlainAccountService accountService;

    @BeforeEach
    void setup() {
        accountService = new PlainAccountService();
    }

    @Test
    void shouldDoNothingWithCorrectPayment() throws InvalidPayment {

        accountService.verifyPayment(LocalDate.now().minusDays(3), new BigDecimal("350"));

    }

    @Test
    void shouldThrowExceptionOfFutureDate() throws InvalidPayment {

        Executable e = () -> accountService.verifyPayment(LocalDate.now().plusDays(10), new BigDecimal("57"));

        Assertions.assertThrows(InvalidPayment.class, e);
    }

    @Test
    void shouldThrowExceptionOfHugeAmount() throws InvalidPayment {

        Executable e = () -> accountService.verifyPayment(LocalDate.now().minusDays(12), new BigDecimal("1023"));

        Assertions.assertThrows(InvalidPayment.class, e);
    }

}