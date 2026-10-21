package com.base.CRM.exception;

public class ProximoPassoAlreadyConcludeException extends RuntimeException {

  public ProximoPassoAlreadyConcludeException() {
    super("Proximo passo já foi finalizado!");
  }
}
