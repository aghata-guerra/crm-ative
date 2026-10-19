package com.base.CRM.exception;

public class LossesPositiveNumberException extends BalanceAmountIncorrectException {

  public LossesPositiveNumberException() {
    super("Perda marcada como número positivo!");
  }
}
