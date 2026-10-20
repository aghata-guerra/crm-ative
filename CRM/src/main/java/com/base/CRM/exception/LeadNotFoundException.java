package com.base.CRM.exception;

public class LeadNotFoundException extends NotFoundException {

  public LeadNotFoundException() {
    super("Lead não foi encontrado!");
  }
}
