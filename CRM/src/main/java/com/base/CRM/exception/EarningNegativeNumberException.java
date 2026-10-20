package com.base.CRM.exception;

public class EarningNegativeNumberException extends BalanceAmountIncorrectException {

  public EarningNegativeNumberException() {
    super("Ganho marcado como número negativo!");
  }
}
