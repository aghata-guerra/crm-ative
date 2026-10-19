package com.base.CRM.entity.dto;

import com.base.CRM.entity.Diagnostico;
import java.time.LocalDate;

public record DiagnosticoDto(
    Long id,
    Long negociacaoId,
    Long userId,
    Integer companySize,
    String currentSituation,
    String mainDifficulty,
    String needs,
    LocalDate updatedAt
) {

  public static DiagnosticoDto fromEntity(Diagnostico diagnostico){
    return new DiagnosticoDto(
        diagnostico.getId(),
        diagnostico.getNegociacao().getId(),
        diagnostico.getUser().getId(),
        diagnostico.getCompanySize(),
        diagnostico.getCurrentSituation(),
        diagnostico.getMainDifficulty(),
        diagnostico.getNeeds(),
        diagnostico.getUpdatedAt()
    );
  }

}
