package dev.karolkoltun.expenses;

import com.sun.source.tree.ArrayAccessTree;
import dev.karolkoltun.currency.CurrencyService;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static dev.karolkoltun.currency.Currency.PLN;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;

/**
 * Prosty serwis do zbierania wydatków.
 */
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

    List<Expense> getExpensesFromCategorySorted(Category category) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().equals(category))
                .sorted(Comparator.comparing(Expense::getAmount))
                .collect(Collectors.toList());
    }

    List<Expense> getExpensesFromPeriod(LocalDate start, LocalDate end) {
        return expenses.stream()
                .filter(expense -> expense.getDate().isAfter(start))
                .filter(expense -> expense.getDate().isBefore(end))
                .collect(Collectors.toList());
    }

    BigDecimal getAverageExpenseInPeriod(LocalDate start, LocalDate end) {
        List<BigDecimal> expenses =
                getExpensesFromPeriod(start, end).stream()
                        .map(Expense::getAmount)
                        .collect(Collectors.toList());
        double[] tab = expenses.toArray();
        BigDecimal sum = new BigDecimal(0);
        for (BigDecimal bd : expenses) {
            sum = sum.add(bd);
        }
        BigDecimal average = sum.divide(BigDecimal.valueOf(expenses.size()));
        return average;

    }

}
