package com.base.CRM.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.base.CRM.entity.Balance;
import com.base.CRM.exception.LossesPositiveNumberException;
import com.base.CRM.repository.BalanceRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

  @InjectMocks
  private BalanceService balanceService;

  @Mock
  private BalanceRepository balanceRepository;

  @Test
  @DisplayName("Deve retornar saldo existente")
  void shouldReturnExistingBalance() {
    Balance balance = new Balance();
    balance.setId(1L);
    balance.setCurrentBalance(BigDecimal.TEN);

    when(balanceRepository.findById(1L)).thenReturn(java.util.Optional.of(balance));

    Balance result = balanceService.overviewBalance();

    assertEquals(BigDecimal.TEN, result.getCurrentBalance());
  }

  @Test
  @DisplayName("Deve criar novo saldo quando nao existir")
  void shouldCreateBalanceWhenNotExists() {
    Balance balance = new Balance();
    balance.setId(1L);

    when(balanceRepository.findById(1L)).thenReturn(java.util.Optional.empty());
    when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

    Balance result = balanceService.overviewBalance();

    assertNotNull(result);
    verify(balanceRepository).save(any(Balance.class));
  }

  @Test
  @DisplayName("Deve adicionar ganhos ao saldo")
  void shouldAddEarnings() {
    Balance balance = new Balance();
    balance.setId(1L);
    balance.setCurrentBalance(BigDecimal.ZERO);
    balance.setTotalEarnings(BigDecimal.ZERO);

    when(balanceRepository.findById(1L)).thenReturn(java.util.Optional.of(balance));
    when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

    balanceService.addBalanceEarnings(new BigDecimal("100.00"));

    verify(balanceRepository).save(any(Balance.class));
  }

  @Test
  @DisplayName("Deve lancar excecao ao adicionar ganho negativo")
  void shouldThrowWhenAddingNegativeEarnings() {
    assertThrows(LossesPositiveNumberException.class,
        () -> balanceService.addBalanceEarnings(new BigDecimal("-10.00")));
  }

  @Test
  @DisplayName("Deve subtrair perdas do saldo")
  void shouldSubtractLosses() {
    Balance balance = new Balance();
    balance.setId(1L);
    balance.setCurrentBalance(BigDecimal.valueOf(100));
    balance.setTotalLosses(BigDecimal.ZERO);

    when(balanceRepository.findById(1L)).thenReturn(java.util.Optional.of(balance));
    when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

    balanceService.subtractBalanceEarnings(new BigDecimal("-30.00"));

    verify(balanceRepository).save(any(Balance.class));
  }

  @Test
  @DisplayName("Deve lancar excecao ao subtrair perda positiva")
  void shouldThrowWhenSubtractingPositiveLoss() {
    assertThrows(LossesPositiveNumberException.class,
        () -> balanceService.subtractBalanceEarnings(new BigDecimal("30.00")));
  }
}
