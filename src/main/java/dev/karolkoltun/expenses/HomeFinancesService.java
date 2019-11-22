package dev.karolkoltun.expenses;

import dev.karolkoltun.currency.CurrencyService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static dev.karolkoltun.currency.Currency.PLN;
import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;

/** Prosty serwis do zbierania wydatków. */
class HomeFinancesService {
  private List<Expense> expenses = new ArrayList<>();

  private CurrencyService currencyService;

  HomeFinancesService(CurrencyService currencyService) {
    this.currencyService = currencyService;
  }

  void addExpense(Expense expense) {
    if (!expense.getCurrency().equals(PLN)) {
      BigDecimal amountInPln =
          currencyService.convert(expense.getAmount(), expense.getCurrency(), PLN);

      expense.setAmount(amountInPln);
      expense.setCurrency(PLN);
    }

    if (expense.getDate().isAfter(now())) {
      return;
    }

    if (expense.getAmount().compareTo(ZERO) < 0) {
      return;
    }

    expenses.add(expense);
  }

  List<Expense> getAllExpenses() {
    return new ArrayList<>(expenses);
  }
}
