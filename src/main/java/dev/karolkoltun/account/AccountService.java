package dev.karolkoltun.account;

import dev.karolkoltun.exceptions.InvalidPayment;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface AccountService {

    void verifyPayment(LocalDate date, BigDecimal amount) throws InvalidPayment;
}
