package com.base.CRM.exception;

public class NegociacaoClosedException extends RuntimeException {

  public NegociacaoClosedException() {
    super("Negociação fechada!");
  }
}
