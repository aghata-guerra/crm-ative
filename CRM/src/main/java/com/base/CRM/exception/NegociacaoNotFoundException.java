package com.base.CRM.exception;

public class NegociacaoNotFoundException extends NotFoundException {

  public NegociacaoNotFoundException() {
    super("Negociacao não foi localizada!");
  }
}
