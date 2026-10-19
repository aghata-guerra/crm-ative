package com.base.CRM.entity.dto;

import com.base.CRM.entity.Atividade;
import com.base.CRM.enumClass.Tipo;
import java.time.LocalDate;

public record AtividadeDto(

    Long id,
    Long negociacaoId,
    Long userId,
    Tipo type,
    String description,
    LocalDate happenDate,
    LocalDate createdAt
) {

  public static AtividadeDto fromEntity(Atividade atividade){
    return new AtividadeDto(
        atividade.getId(),
        atividade.getNegociacao().getId(),
        atividade.getUser().getId(),
        atividade.getType(),
        atividade.getDescription(),
        atividade.getHappenDate(),
        atividade.getCreatedAt()
    );
  }

}
