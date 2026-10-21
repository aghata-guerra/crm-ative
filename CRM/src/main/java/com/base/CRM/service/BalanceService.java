package com.base.CRM.service;

import com.base.CRM.entity.Balance;
import com.base.CRM.exception.LossesPositiveNumberException;
import com.base.CRM.repository.BalanceRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

  private final BalanceRepository balanceRepository;

  public BalanceService(BalanceRepository balanceRepository) {
    this.balanceRepository = balanceRepository;
  }

  public Balance overviewBalance(){
    return balanceRepository.findById(1L).orElseGet(
        () -> balanceRepository.save(new Balance())
    );
  }

  @Transactional
  public void addBalanceEarnings(BigDecimal earnings){

    if(earnings.compareTo(BigDecimal.ZERO) >= 0){

      Balance balance = overviewBalance();
      balance.setTotalEarnings(balance.getTotalEarnings().add(earnings));
      balance.setCurrentBalance(balance.getCurrentBalance().add(earnings));
      balanceRepository.save(balance);

    } else {
      throw new LossesPositiveNumberException();
    }


  }

  @Transactional
  public void subtractBalanceEarnings(BigDecimal losses){

    if(losses.compareTo(BigDecimal.ZERO) <= 0){
      Balance balance = overviewBalance();
      balance.setTotalLosses(balance.getTotalLosses().add(losses));
      balance.setCurrentBalance(balance.getCurrentBalance().add(losses));
      balanceRepository.save(balance);

    } else {
      throw new LossesPositiveNumberException();
    }


  }


}
