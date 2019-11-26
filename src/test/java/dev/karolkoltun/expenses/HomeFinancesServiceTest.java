package dev.karolkoltun.expenses;

import dev.karolkoltun.currency.Currency;
import dev.karolkoltun.currency.CurrencyService;
import dev.karolkoltun.currency.DynamicCurrencyService;
import dev.karolkoltun.currency.PlainCurrencyService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HomeFinancesServiceTest {

    private HomeFinancesService homeFinancesService;
    private PlainCurrencyService plainCurrencyService;

    @BeforeEach
    void setup(){
        plainCurrencyService = new PlainCurrencyService();
        homeFinancesService = new HomeFinancesService(plainCurrencyService);
    }

    @Test
    void shouldAddExpenseInPLN(){
        //Given
        Expense expense = new Expense(LocalDate.now().minusDays(1), new BigDecimal(345), "CSK", Category.OTHERS);

        //When
        homeFinancesService.addExpense(expense);
        List<Expense> actualExpense = homeFinancesService.getAllExpenses();
        String where = actualExpense.get(0).getWhere();
        Category actualCategory = actualExpense.get(0).getCategory();

        //Then
        assertThat(actualExpense)
                .as("List should contain added expense")
                .contains(expense);

        Expense addedExpense = actualExpense.get(0);

        assertThat(addedExpense)
                .extracting("where", "amount")
                .containsExactly("CSK", BigDecimal.valueOf(345));

    }

    @Test
    void shouldntAddExpenseWithFutureDate(){
        //Given
        Expense expense = new Expense(LocalDate.now().plusDays(3), new BigDecimal(120), "Rossmann", Category.GIFTS);

        //When
        homeFinancesService.addExpense(expense);
        List<Expense> actualExpense = homeFinancesService.getAllExpenses();

        //Then
        assertFalse(actualExpense.contains(expense));
    }

    @Test
    void shouldntAddExpenseWithNegativeAmount(){
        //Given
        Expense expense = new Expense(LocalDate.now().minusDays(3), new BigDecimal(-45), "Rossmann", Category.GIFTS);

        //When
        homeFinancesService.addExpense(expense);
        List<Expense> actualExpense = homeFinancesService.getAllExpenses();

        //Then
        assertTrue(actualExpense.isEmpty());
    }

    @Test
    void shouldAddExpenseInForeignCurrency(){
        // Given
        CurrencyService currencyServiceMock =
                Mockito.mock(CurrencyService.class);
        HomeFinancesService homeFinancesService =
                new HomeFinancesService(currencyServiceMock);
        Expense expenseInEuros = new Expense(
                LocalDate.now().minusDays(2),
                BigDecimal.valueOf(500),
                "Pizzeria Stefano",
                Category.FOOD,
                Currency.EUR);

        when(currencyServiceMock.convert(
                any(BigDecimal.class),
                eq(Currency.EUR),
                eq(Currency.PLN))).thenReturn(BigDecimal.valueOf(400));

        // When
        homeFinancesService.addExpense(expenseInEuros);

        // Then
        List<Expense> expenses = homeFinancesService.getAllExpenses();
        assertThat(expenses).containsExactly(expenseInEuros);
        Expense addedExpense = expenses.get(0);
        assertThat(addedExpense)
                .hasFieldOrPropertyWithValue("currency", Currency.PLN);
        assertEquals(BigDecimal.valueOf(400), addedExpense.getAmount());

        verify(currencyServiceMock, times(1)).convert(BigDecimal.valueOf(500), Currency.EUR, Currency.PLN);

    }

    @Test
    void shouldntGetExpenseOutOfCategory(){
        //Given
        Expense expense1 = new Expense(LocalDate.now().minusDays(3), new BigDecimal(50), "Rossmann", Category.GIFTS);
        Expense expense2 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(80), "Drogeria Rossmann", Category.OTHERS);
        Expense expense3 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(60), "BP", Category.CARS);
        Expense expense4 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(25), "Rossmann", Category.OTHERS);
        Expense expense5 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(50), "Rossmann", Category.OTHERS);

        //When
        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);
        homeFinancesService.addExpense(expense3);
        homeFinancesService.addExpense(expense4);
        homeFinancesService.addExpense(expense5);

        //Then
        List<Expense> actualList = homeFinancesService.getExpensesFromCategorySorted(Category.OTHERS);
        assertThat(actualList)
                .allMatch(expense -> expense.getCategory().equals(Category.OTHERS))
                .isSortedAccordingTo(Comparator.comparing(Expense::getAmount));
    }

    @Test
    void shouldGetExpensesFormPeriod(){
        // Given
        Expense expense1 = new Expense(LocalDate.of(2019, 11, 13), BigDecimal.valueOf(45), "Empik", Category.GIFTS);
        Expense expense2 = new Expense(LocalDate.of(2019, 11, 20), BigDecimal.valueOf(20), "KFC", Category.FOOD);

        // When
        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);

        //Then
        List<Expense> expensesFromPeriod = homeFinancesService
                .getExpensesFromPeriod(LocalDate.of(2019, 11, 5), LocalDate.of(2019, 11, 15));
        assertEquals(1, expensesFromPeriod.size());
        Expense expenseFromPeriod = expensesFromPeriod.get(0);
        assertThat(expenseFromPeriod)
                .isEqualTo(expense1);
    }

    @Test
    void shouldGetAverageExpenseInPeriod() {
        // Given
        Expense expense1 = new Expense(LocalDate.of(2019, 11, 13), BigDecimal.valueOf(45), "Empik", Category.GIFTS);
        Expense expense2 = new Expense(LocalDate.of(2019, 11, 21), BigDecimal.valueOf(30), "KFC", Category.FOOD);
        Expense expense3 = new Expense(LocalDate.of(2019, 11, 22), BigDecimal.valueOf(50), "MC", Category.FOOD);
        Expense expense4 = new Expense(LocalDate.of(2019, 11, 23), BigDecimal.valueOf(14), "MC", Category.FOOD);
        BigDecimal expectedAverage = BigDecimal.valueOf(31.33);

        // When
        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);
        homeFinancesService.addExpense(expense3);
        homeFinancesService.addExpense(expense4);


        // Then
        BigDecimal actualAverage = homeFinancesService.
                getAverageExpenseInPeriod(LocalDate.of(2019,11,20), LocalDate.of(2019,11,25));
        assertEquals(expectedAverage, actualAverage);

    }

}