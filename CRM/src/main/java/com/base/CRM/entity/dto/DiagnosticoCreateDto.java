package com.base.CRM.entity.dto;

import com.base.CRM.entity.Diagnostico;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.entity.User;

public record DiagnosticoCreateDto(
    Long negociacaoId,
    Long userId,
    Integer companySize,
    String currentSituation,
    String mainDifficulty,
    String needs
) {

  public Diagnostico toEntity(Negociacao negociacao, User user){
    return new Diagnostico(
      null,
      negociacao,
      user,
      companySize,
      currentSituation,
      mainDifficulty,
      needs
    );
  }
}
