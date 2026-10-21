package com.base.CRM.entity.dto;

import com.base.CRM.entity.Lead;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.Origin;
import com.base.CRM.enumClass.StatusNegociacao;
import java.math.BigDecimal;

public record NegociacaoCreateDto(
    Long leadId,
    Long userId,
    Origin origin,
    Etapa stage,
    BigDecimal estimatedCost
) {

  public Negociacao toEntity(Lead lead, User user){
    return new Negociacao(
        null,
        lead,
        user,
        origin,
        stage,
        estimatedCost
    );
  }

}
