package com.base.CRM.exception;

public class StatusProximoPassoCantReturnException extends RuntimeException {

  public StatusProximoPassoCantReturnException() {
    super("Próximo passo não pode ser alterada após ser marcada como realizada!");
  }
}
