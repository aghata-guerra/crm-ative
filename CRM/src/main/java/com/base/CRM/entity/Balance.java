package com.base.CRM.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "balance")
public class Balance {

  @Id
  private Long id = 1L;

  @Column(nullable = false)
  private BigDecimal currentBalance = BigDecimal.ZERO;
  @Column(nullable = false)
  private BigDecimal totalEarnings = BigDecimal.ZERO;
  @Column(nullable = false)
  private BigDecimal totalLosses = BigDecimal.ZERO;

  public Balance() {
  }

  public Balance(Long id, BigDecimal currentBalance, BigDecimal totalEarnings,
      BigDecimal totalLosses) {
    this.id = id;
    this.currentBalance = currentBalance;
    this.totalEarnings = totalEarnings;
    this.totalLosses = totalLosses;
  }

  public BigDecimal getTotalLosses() {
    return totalLosses;
  }

  public void setTotalLosses(BigDecimal totalLosses) {
    this.totalLosses = totalLosses;
  }

  public BigDecimal getTotalEarnings() {
    return totalEarnings;
  }

  public void setTotalEarnings(BigDecimal totalEarnings) {
    this.totalEarnings = totalEarnings;
  }

  public BigDecimal getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(BigDecimal currentBalance) {
    this.currentBalance = currentBalance;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
