package dev.karolkoltun.expenses;

import dev.karolkoltun.currency.Currency;
import dev.karolkoltun.currency.CurrencyService;
import dev.karolkoltun.currency.PlainCurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static dev.karolkoltun.expenses.Category.*;
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
        Expense expense = new Expense(LocalDate.now().minusDays(1), new BigDecimal(345), "CSK", OTHERS);

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
        Expense expense = new Expense(LocalDate.now().plusDays(3), new BigDecimal(120), "Rossmann", GIFTS);

        //When
        homeFinancesService.addExpense(expense);
        List<Expense> actualExpense = homeFinancesService.getAllExpenses();

        //Then
        assertFalse(actualExpense.contains(expense));
    }

    @Test
    void shouldntAddExpenseWithNegativeAmount(){
        //Given
        Expense expense = new Expense(LocalDate.now().minusDays(3), new BigDecimal(-45), "Rossmann", GIFTS);

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
                FOOD,
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
    void shouldGetExpensesFromCategory(){
        //Given
        Expense expense1 = new Expense(LocalDate.now().minusDays(3), new BigDecimal(50), "Rossmann", GIFTS);
        Expense expense2 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(80), "Drogeria Rossmann", OTHERS);
        Expense expense3 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(60), "BP", CARS);
        Expense expense4 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(25), "Rossmann", OTHERS);
        Expense expense5 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(50), "Rossmann", OTHERS);

        //When
        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);
        homeFinancesService.addExpense(expense3);
        homeFinancesService.addExpense(expense4);
        homeFinancesService.addExpense(expense5);

        //Then
        List<Expense> actualList = homeFinancesService.getExpensesFromCategory(OTHERS);
        assertThat(actualList)
                .allMatch(expense -> expense.getCategory().equals(OTHERS))
                .isSortedAccordingTo(Comparator.comparing(Expense::getAmount));
    }

    @Test
    void shouldGiveEmptyList(){
        // Given
        Expense expense1 = new Expense(LocalDate.now().minusDays(3), new BigDecimal(50), "Rossmann", GIFTS);
        Expense expense2 = new Expense(LocalDate.now().minusDays(4), new BigDecimal(80), "Drogeria Rossmann", OTHERS);

        // When
        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);

        // Then
        List<Expense> actual = homeFinancesService.getExpensesFromCategory(CARS);
        assertThat(actual.isEmpty());

    }

    @Test
    void shouldGetExpensesFormPeriod(){
        // Given
        Expense expense1 = new Expense(LocalDate.of(2019, 11, 13), BigDecimal.valueOf(45), "Empik", GIFTS);
        Expense expense2 = new Expense(LocalDate.of(2019, 11, 20), BigDecimal.valueOf(20), "KFC", FOOD);

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
        Expense expense1 = new Expense(LocalDate.of(2019, 11, 13), BigDecimal.valueOf(45), "Empik", GIFTS);
        Expense expense2 = new Expense(LocalDate.of(2019, 11, 21), BigDecimal.valueOf(30), "KFC", FOOD);
        Expense expense3 = new Expense(LocalDate.of(2019, 11, 22), BigDecimal.valueOf(50), "MC", FOOD);
        Expense expense4 = new Expense(LocalDate.of(2019, 11, 23), BigDecimal.valueOf(14), "MC", FOOD);
        BigDecimal expectedAverage = BigDecimal.valueOf(31.33);

        // When
        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);
        homeFinancesService.addExpense(expense3);
        homeFinancesService.addExpense(expense4);

        // Then
        BigDecimal actualAverage = homeFinancesService.
                getAverageExpenseInPeriod(LocalDate.of(2019,11,20), LocalDate.of(2019,11,25));
        assertThat(actualAverage).isEqualByComparingTo(expectedAverage);
    }

    @Test
    void shouldGet0AsAverageExpense(){
        // Given
        Expense expense1 = new Expense(LocalDate.of(2019, 11, 21), BigDecimal.valueOf(30), "KFC", FOOD);
        Expense expense2 = new Expense(LocalDate.of(2019, 11, 22), BigDecimal.valueOf(50), "MC", FOOD);
        BigDecimal expectedAverage = BigDecimal.ZERO;

        //When
        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);

        // Then
        BigDecimal actualAverage = homeFinancesService
                .getAverageExpenseInPeriod(LocalDate.of(2019, 11, 5), LocalDate.of(2019, 11, 14));
        assertEquals(expectedAverage, actualAverage);
    }

    @Test
    void shouldGetBiggestAmountFromCategory() throws NoExpenseInCategoryException{
        // Given
        Expense expense1 = new Expense(LocalDate.now().minusDays(1), BigDecimal.valueOf(80), "CCC", OTHERS);
        Expense expense2 = new Expense(LocalDate.now().minusDays(1), BigDecimal.valueOf(40), "CCC", OTHERS);
        Expense expense3 = new Expense(LocalDate.now().minusDays(1), BigDecimal.valueOf(120), "Zara", OTHERS);
        Expense expense4 = new Expense(LocalDate.now().minusDays(1), BigDecimal.valueOf(180), "Shop", GIFTS);

        // When
        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);
        homeFinancesService.addExpense(expense3);
        homeFinancesService.addExpense(expense4);

        // Then
        Expense actualExpense = homeFinancesService.getBiggestExpenseFrom(OTHERS);
        assertEquals(expense3, actualExpense);
    }

    @Test
    void shouldThrowNoExpenseException() throws NoExpenseInCategoryException {
        // Given
        Expense expense1 = new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(100), "Lidl", FOOD);
        Expense expense2 = new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(130), "Lidl", OTHERS);

        homeFinancesService.addExpense(expense1);
        homeFinancesService.addExpense(expense2);

        Executable ex = () -> homeFinancesService.getBiggestExpenseFrom(GIFTS);

        assertThrows(NoExpenseInCategoryException.class, ex);
    }

    @Test
    void shouldGetTheBiggestExpenseFromEachCategory() throws NoExpenseInCategoryException {
        // Given
        List<Expense> expenses = List.of(
        new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(100), "Lidl", FOOD),
        new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(150), "Lidl", FOOD),
        new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(100), "BP", CARS),
        new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(150), "BP", CARS),
        new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(100), "Apart", GIFTS),
        new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(250), "Apart", GIFTS),
        new Expense(LocalDate.now().minusDays(3), BigDecimal.valueOf(50), "Shop", OTHERS));
        Map<Category, BigDecimal> expectedMap = Map.of(
                FOOD, BigDecimal.valueOf(150),
                CARS, BigDecimal.valueOf(150),
                GIFTS, BigDecimal.valueOf(250),
                OTHERS, BigDecimal.valueOf(50));

        // When
        for(Expense ex : expenses){
            homeFinancesService.addExpense(ex);
        }

        //Then
        Map<Category, BigDecimal> actualMap = homeFinancesService.getBiggestExpenseFromEachCategory();
        assertEquals(expectedMap.size(), actualMap.size());

        for (Category cat : Category.values()) {
            assertThat(actualMap.get(cat)).isEqualByComparingTo(expectedMap.get(cat));
        }
    }

    @Test
    void shouldGetZeroAmountForEachCategory(){
        // Given
        Map<Category, BigDecimal> expectedMap = Map.of(
                FOOD, BigDecimal.ZERO,
                CARS, BigDecimal.ZERO,
                GIFTS, BigDecimal.ZERO,
                OTHERS, BigDecimal.ZERO);

        //When
        Map<Category, BigDecimal> actualMap = homeFinancesService.getBiggestExpenseFromEachCategory();

        //Then
        assertEquals(expectedMap.size(), actualMap.size());
        assertThat(actualMap)
                .containsKeys(FOOD, CARS, OTHERS, GIFTS)
                .containsValue(BigDecimal.ZERO);
    }

    @Test
    void shouldGetAverageExpenseFromEachCategory() {
        List<Expense> expenses = List.of(
        new Expense(LocalDate.now().minusDays(2), BigDecimal.valueOf(58), "Shop", FOOD),
        new Expense(LocalDate.now().minusDays(2), BigDecimal.valueOf(40), "Shop", FOOD),
        new Expense(LocalDate.now().minusDays(2), BigDecimal.valueOf(95), "BP", CARS),
        new Expense(LocalDate.now().minusDays(2), BigDecimal.valueOf(68), "Bp", CARS),
        new Expense(LocalDate.now().minusDays(2), BigDecimal.valueOf(100), "Bp", CARS),
        new Expense(LocalDate.now().minusDays(2), BigDecimal.valueOf(35), "Shop", OTHERS));
        Map<Category, BigDecimal> expectedMap = Map.of(
                FOOD, BigDecimal.valueOf(49),
                CARS, BigDecimal.valueOf(87.67),
                OTHERS, BigDecimal.valueOf(35),
                GIFTS, BigDecimal.ZERO);

        for(Expense exp : expenses){
            homeFinancesService.addExpense(exp);
        }

        Map<Category, BigDecimal> actualMap = homeFinancesService.getAveragesByCategory();

        assertEquals(expectedMap.size(), actualMap.size());
        for(Category cat : Category.values()){
            assertThat(actualMap.get(cat)).isEqualByComparingTo(expectedMap.get(cat));
        }
    }

    @Test
    void whichExpense()throws NoExpenseInCategoryException{
        Expense exp2 = new Expense(LocalDate.now().minusDays(4), BigDecimal.valueOf(58), "Shop", FOOD);
        Expense exp1 = new Expense(LocalDate.now().minusDays(2), BigDecimal.valueOf(58), "Shop", FOOD);
        homeFinancesService.addExpense(exp2);
        homeFinancesService.addExpense(exp1);
        Expense mistery = homeFinancesService.getBiggestExpenseFrom(FOOD);
        System.out.println(exp1);
        System.out.println(exp2);
        System.out.println(mistery);
    }

}