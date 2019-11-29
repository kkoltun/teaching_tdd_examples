package dev.karolkoltun.expenses;

import dev.karolkoltun.account.AccountService;
import dev.karolkoltun.account.BankAccount;
import dev.karolkoltun.currency.CurrencyService;
import dev.karolkoltun.exceptions.InvalidPayment;
import dev.karolkoltun.exceptions.NoExpenseInCategoryException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private AccountService accountService;

    HomeFinancesService(CurrencyService currencyService, AccountService accountService) {
        this.currencyService = currencyService;
        this.accountService = accountService;
    }

    void addExpense(Expense expense) throws InvalidPayment {

        accountService.verifyPayment(expense.getDate(), expense.getAmount());

        if (!expense.getCurrency().equals(PLN)) {
            BigDecimal amountInPln =
                    currencyService.convert(expense.getAmount(), expense.getCurrency(), PLN);

            expense.setAmount(amountInPln);
            expense.setCurrency(PLN);
        }

        if (expense.getAmount().compareTo(ZERO) < 0) {
            return;
        }

        expenses.add(expense);
    }

    List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    List<Expense> getExpensesFromCategory(Category category) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().equals(category))
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
        if (expenses.isEmpty()) {
            return ZERO;
        } else {
            return computeAverage(expenses);
        }
    }

    Expense getBiggestExpenseFrom(Category category) throws NoExpenseInCategoryException {
        List<Expense> maxExpense = getAllExpenses().stream()
                .filter(expense -> expense.getCategory().equals(category))
                .sorted(Comparator.comparing(Expense::getAmount).reversed())
                .limit(1)
                .collect(Collectors.toList());
        if (!maxExpense.isEmpty()) {
            return maxExpense.get(0);
        } else {
            throw new NoExpenseInCategoryException();
        }
    }

    Map<Category, BigDecimal> getBiggestExpenseFromEachCategory() {
        Map<Category, BigDecimal> biggestExpenses = new HashMap<>();
        for (Category cat : Category.values()) {
            List<BigDecimal> list = getExpensesFromCategory(cat).stream()
                    .map(Expense::getAmount)
                    .sorted(Comparator.reverseOrder())
                    .limit(1)
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                biggestExpenses.put(cat, ZERO);
            } else {
                biggestExpenses.put(cat, list.get(0));
            }
        }
        return biggestExpenses;
    }

    Map<Category, BigDecimal> getAveragesByCategory() {
        Map<Category, BigDecimal> averageExpenses = new HashMap<>();

        for (Category cat : Category.values()) {
            List<BigDecimal> list = getAllExpenses().stream()
                    .filter(expense -> expense.getCategory().equals(cat))
                    .map(Expense::getAmount)
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                averageExpenses.put(cat, ZERO);
            } else {
                BigDecimal average = computeAverage(list);
                averageExpenses.put(cat, average);
            }
        }
        return averageExpenses;
    }

    private BigDecimal computeAverage(List<BigDecimal> list) {
        BigDecimal sum = ZERO;
        for (BigDecimal bd : list) {
            sum = sum.add(bd);
        }
        return sum.divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
    }
}
