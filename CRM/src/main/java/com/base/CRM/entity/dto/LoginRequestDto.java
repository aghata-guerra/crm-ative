package com.base.CRM.entity.dto;

import jakarta.validation.constraints.*;

public record LoginRequestDto(
    @NotBlank @Email String email,
    @NotBlank String password
) {

}
