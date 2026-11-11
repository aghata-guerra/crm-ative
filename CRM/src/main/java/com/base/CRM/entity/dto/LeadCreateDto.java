package com.base.CRM.entity.dto;

import jakarta.validation.constraints.*;


import com.base.CRM.entity.Empresa;
import com.base.CRM.entity.Lead;
import com.base.CRM.enumClass.Origin;
import java.util.Locale;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LeadCreateDto(
    @NotBlank(message = "Nome não pode estar em branco")
    @Size(min = 3, max = 100, message = "O nome precisa ter entre 3 e 100 caracteres")
    String name,
    @NotBlank(message = "O email precisa ser preenchido")
    @Email(message = "Email inválido")
    @Size(max = 255)
    String email,
    @NotBlank(message = "O telefone precisa ser preenchido")
    @Pattern(regexp = "[0-9]{10,12}", message = "Informe um telefone com 10 a 12 dígitos")
    String telephone,
    @NotBlank(message = "A posição precisa ser preenchida")
    @Size(max = 255)
    String position,
    @NotNull(message = "A empresa precisa ser preenchida")
    Long empresaId,
    Origin origin
) {

  public LeadCreateDto(String name, String email, String telephone, String position, Long empresaId) {
    this(name, email, telephone, position, empresaId, Origin.DESCONHECIDA);
  }

  public Lead toEntity(Empresa empresa){

    Lead lead = new Lead(
        null,
        name.trim(),
        email.trim().toLowerCase(Locale.ROOT),
        telephone.trim(),
        position.trim(),
        empresa
    );
    lead.setOrigin(origin == null ? Origin.DESCONHECIDA : origin);
    return lead;

  }

}
