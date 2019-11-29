package dev.karolkoltun.calculator;

import dev.karolkoltun.exceptions.DivisionByZeroException;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Wyjatkowo prosty kalkulator. */
class SimpleCalculator {
  int add(int x, int y) {
    return x + y;
  }

  int subtract(int x, int y) {
    return x - y;
  }

  int multiply(int x, int y) {
    return x * y;
  }

  double divide(double x, double y) throws DivisionByZeroException {
    if(y == 0){
      throw new DivisionByZeroException(x +"/"+ y +" is illegal.");
    }
    return x / y;
  }

  boolean isOdd(int x){
    return (x%2 != 0);
  }

  boolean isNumber(String text){
    return NumberUtils.isCreatable(text);
  }

  List<Integer> getDivisors(int x){
    List<Integer> divisors = new ArrayList<>();
    for(int i = 1; i <= x/2; i++){
      if(x % i == 0) {
        addIfListDoesNotContains(divisors, i);
        addIfListDoesNotContains(divisors, x/i);
      }
    }
    return divisors;
  }

  private void addIfListDoesNotContains(List<Integer> list, int x){
    if(!list.contains(x)){
      list.add(x);
    }
  }

  int calculateFibonacci(int n) {
    if (n <= 1) {
      return n;
    }
    return calculateFibonacci(n - 1) + calculateFibonacci(n - 2);
  }

  BigDecimal calculateGrossAmount(BigDecimal amount, BigDecimal percentage) {
    final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;
    final int SCALE = 2;
    BigDecimal tax = amount
            .multiply(percentage)
            .divide(new BigDecimal("100"), 2, DEFAULT_ROUNDING);
    return amount.add(tax);
  }
}
