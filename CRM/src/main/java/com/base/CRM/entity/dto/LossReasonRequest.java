package com.base.CRM.entity.dto;

import jakarta.validation.constraints.NotBlank;

public record LossReasonRequest(
    @NotBlank(message = "O motivo da perda é obrigatório!")
    @jakarta.validation.constraints.Size(max = 255)
        String lossReason
) {
}
