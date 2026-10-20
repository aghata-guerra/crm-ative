package com.base.CRM.exception;

public class EmpresaAlreadyExistsException extends AlreadyExistsException {

  private Long empresaId;

  public EmpresaAlreadyExistsException(Long empresaId, String message) {
    super(message);
    this.empresaId = empresaId;
  }

  public Long getLeadId() {
    return empresaId;
  }
}
