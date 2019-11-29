package dev.karolkoltun.account;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    private BankAccount bankAccount;

    @BeforeEach
    void setup(){
        bankAccount = new BankAccount(new BigDecimal("1000"));
    }

    @Test
    void shouldDepositAmountOnAccount(){
        //Given
        BigDecimal amount = new BigDecimal("250");
        BigDecimal expectedBalance = new BigDecimal("1250");

        //When
        bankAccount.deposit(amount);
        BigDecimal actualBalance = bankAccount.getBalance();

        //Then
        assertThat(actualBalance).isEqualByComparingTo(expectedBalance);
    }

    @Test
    void shouldThrowExceptionWhenNegativeAmount() {
        //Given
        BigDecimal amount = new BigDecimal("-100");

        Executable ex = () -> bankAccount.deposit(amount);

        assertThrows(IllegalStateException.class, ex);
    }

    @Test
    void shouldWithdrawAmount(){
        // Given
        BigDecimal amount = new BigDecimal("300");
        BigDecimal expectedBalance = new BigDecimal("700");

        // When
        bankAccount.withdraw(amount);
        BigDecimal actualBalance = bankAccount.getBalance();

        // Then
        assertThat(actualBalance).isEqualByComparingTo(expectedBalance);
    }

    @Test
    void shouldWithdrawAmountAndMakeDebt(){
        // Given
        BigDecimal amount = new BigDecimal("1200");
        BigDecimal expectedBalance = new BigDecimal("-205");

        // When
        bankAccount.withdraw(amount);
        BigDecimal actualBalance = bankAccount.getBalance();

        // Then
        assertThat(actualBalance).isEqualByComparingTo(expectedBalance);
    }

    @Test
    void shouldThrowExceptionWhenDebtOverdrawn() {
        //Given
        bankAccount.withdraw(new BigDecimal("1500"));
        BigDecimal amount = new BigDecimal("700");

        // When
        Executable ex = () -> bankAccount.withdraw(amount);

        // Then
        assertThrows(IllegalStateException.class, ex);
    }

    @Test
    void shouldChargeDebtOnce(){
        // Given
        BigDecimal amount1 = new BigDecimal("1100");
        BigDecimal amount2 = new BigDecimal("100");
        BigDecimal expected = new BigDecimal("-205");
        // When
        bankAccount.withdraw(amount1);
        bankAccount.withdraw(amount2);
        BigDecimal actual = bankAccount.getBalance();
        // Then
        assertThat(actual).isEqualByComparingTo(expected);
    }

}