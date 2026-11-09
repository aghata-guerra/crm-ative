package com.base.CRM.exception;

public class StageChangeException extends RuntimeException {

  public StageChangeException() {
    super("Erro ao fazer a mudança de Etapa da negociação");
  }
}
