package com.base.CRM.entity.dto;

import com.base.CRM.entity.Diagnostico;
import com.base.CRM.entity.Negociacao;
import com.base.CRM.enumClass.Etapa;
import com.base.CRM.enumClass.Origin;
import com.base.CRM.enumClass.StatusNegociacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record NegociacaoDto(
    Long id,
    Long leadId,
    Long userId,
    Origin origin,
    Etapa stage,
    StatusNegociacao statusNegociacao,
    BigDecimal estimatedCost,
    LocalDate startDate,
    LocalDate finalDate,
    LocalDate updatedStageDate,
    List<AtividadeDto> atividadeDtoList,
    List<ProximoPassoDto> proximoPassoDtoList,
    List<DiagnosticoDto> diagnosticoDtoList
) {

  public static NegociacaoDto fromEntity(Negociacao negociacao){
    return new NegociacaoDto(
        negociacao.getId(),
        negociacao.getLead().getId(),
        negociacao.getUser().getId(),
        negociacao.getOrigin(),
        negociacao.getStage(),
        negociacao.getStatusNegociacao(),
        negociacao.getEstimatedCost(),
        negociacao.getStartDate(),
        negociacao.getFinalDate() != null
            ? negociacao.getFinalDate() : null,
        negociacao.getUpdatedStageDate() != null
            ? negociacao.getUpdatedStageDate() : null,
        negociacao.getAtividadeList() != null
            ? negociacao.getAtividadeList().stream().toList().stream().map(AtividadeDto :: fromEntity).toList()
            : null,
        negociacao.getProximoPasso() != null
            ? negociacao.getProximoPasso().stream().map(ProximoPassoDto :: fromEntity).toList()
            : null,
        negociacao.getDiagnosticoList() != null
            ? negociacao.getDiagnosticoList().stream().map(DiagnosticoDto :: fromEntity).toList()
            : null
    );
  }

}
