package dev.karolkoltun.account;

import java.math.BigDecimal;

public class BankAccount {

    private final BigDecimal MAX_DEBT = new BigDecimal("-1000");
    private final BigDecimal DEBT_CHARGE = new BigDecimal("5");

    private BigDecimal balance;

    public BankAccount(BigDecimal balance){
        this.balance = balance;
    }

    void deposit(BigDecimal amount) throws IllegalStateException {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalStateException();
        }
        BigDecimal newBalance = getBalance().add(amount);
        setBalance(newBalance);
    }

    void withdraw(BigDecimal amount){
        if(getBalance().subtract(amount).compareTo(MAX_DEBT) < 0){
            throw new IllegalStateException();
        }
        if(amount.compareTo(getBalance()) > 0){
            chargeClientWithDebt(amount);
            BigDecimal newBalance = getBalance().subtract(amount);
            setBalance(newBalance);
        } else{
            BigDecimal newBalance = getBalance().subtract(amount);
            setBalance(newBalance);
        }
    }

    BigDecimal getBalance(){
        return balance;
    }

    private void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    private void chargeClientWithDebt(BigDecimal amount){
        if((getBalance().compareTo(BigDecimal.ZERO) > 0) && (getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0)){
            setBalance(getBalance().subtract(DEBT_CHARGE));
        }
    }
}
