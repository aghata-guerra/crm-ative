package com.base.CRM.entity.dto;

import jakarta.validation.constraints.*;

import com.base.CRM.entity.Empresa;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmpresaCreateDto(
    @JsonProperty("razao_social")
    @NotBlank @Size(max=255) String companyName,
    @JsonProperty("nome_fantasia")
    String tradeName,
    @NotBlank @Pattern(regexp="[0-9./-]{14,18}") String cnpj,
    @JsonProperty("telefones")
    List<TelefoneCreateDto> telephones,
    @Email String email
) {

  public Empresa toEntity(){

    String telephoneCreate = telephones != null && !telephones().isEmpty()
        ? telephones().get(0).getCompleteTelephone()
        : null;

    return new Empresa(
        null,
        companyName,
        tradeName,
        cnpj == null ? null : cnpj.replaceAll("[^0-9]", ""),
        telephoneCreate,
        email
    );
  }

}
