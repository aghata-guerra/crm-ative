package com.base.CRM.entity.dto;

import com.base.CRM.entity.Empresa;
import java.time.LocalDateTime;
import java.util.List;

public record EmpresaDto(
    Long id,
    String companyName,
    String tradeName,
    String cnpj,
    String email,
    String telephone,
    LocalDateTime createdAt,
    List<LeadDto> leadList
) {

  public static EmpresaDto fromEntity(Empresa empresa){
    return new EmpresaDto(
        empresa.getId(),
        empresa.getCompanyName(),
        empresa.getTradeName(),
        empresa.getCnpj(),
        empresa.getEmail(),
        empresa.getTelephone(),
        empresa.getCreatedAt(),
        empresa.getLeadList().stream().map(LeadDto :: fromEntity).toList()
    );
  }


}
