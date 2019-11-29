package dev.karolkoltun.account;

import dev.karolkoltun.exceptions.InvalidPayment;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PlainAccountService implements AccountService{

    private final BigDecimal MAX_AMOUNT = new BigDecimal("1000");

    @Override
    public void verifyPayment(LocalDate date, BigDecimal amount) throws InvalidPayment {
        if(date.isAfter(LocalDate.now()) || (amount.compareTo(MAX_AMOUNT) > 0)){
            throw new InvalidPayment();
        }
    }
}
