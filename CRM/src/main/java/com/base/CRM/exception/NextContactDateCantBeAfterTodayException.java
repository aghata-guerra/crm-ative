package com.base.CRM.exception;

public class NextContactDateCantBeAfterTodayException extends RuntimeException {

  public NextContactDateCantBeAfterTodayException() {
    super("A data do próximo contato não pode ser depois do dia atual!");
  }
}
