package com.base.CRM.exception;

public class LeadAlreadyExistsException extends AlreadyExistsException {

  private Long leadId;

  public LeadAlreadyExistsException(String message, Long leadId) {
    super(message);
    this.leadId = leadId;
  }

  public Long getLeadId() {
    return leadId;
  }
}
