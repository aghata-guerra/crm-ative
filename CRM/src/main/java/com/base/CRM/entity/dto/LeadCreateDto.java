package com.base.CRM.entity.dto;


import com.base.CRM.entity.Empresa;
import com.base.CRM.entity.Lead;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LeadCreateDto(
    @NotBlank(message = "Nome não pode estar em branco")
    @Size(min = 0, max = 100, message = "O nome precisa ter entre 3 e 100 caracteres")
    String name,
    @Email(message = "Email inválido")
    String email,
    @NotBlank(message = "O telefone precisa ser preenchido")
    @Size(min = 0, max = 12, message = "O telegone precisa ter entre 3 e 12 caracteres")
    String telephone,
    @NotBlank(message = "A posição precisa ser preenchida")
    String position,
    @NotNull(message = "A empresa precisa ser preenchida")
    Long empresaId
) {

  public Lead toEntity(Empresa empresa){

    return new Lead(
        null,
        name,
        email,
        telephone,
        position,
        empresa
    );

  }

}
