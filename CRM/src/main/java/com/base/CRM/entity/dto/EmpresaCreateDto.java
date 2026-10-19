package com.base.CRM.entity.dto;

import com.base.CRM.entity.Empresa;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmpresaCreateDto(
    @JsonProperty("razao_social")
    String companyName,
    @JsonProperty("nome_fantasia")
    String tradeName,
    String cnpj,
    @JsonProperty("telefones")
    List<TelefoneCreateDto> telephones,
    String email
) {

  public Empresa toEntity(){

    String telephoneCreate = telephones != null && !telephones().isEmpty()
        ? telephones().get(0).getCompleteTelephone()
        : null;

    return new Empresa(
        companyName,
        tradeName,
        cnpj,
        telephoneCreate,
        email
    );
  }

}
