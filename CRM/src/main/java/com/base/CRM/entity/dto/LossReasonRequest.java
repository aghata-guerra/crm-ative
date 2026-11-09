package com.base.CRM.entity.dto;

import jakarta.validation.constraints.NotBlank;

public record LossReasonRequest(
    @NotBlank(message = "O motivo da perda é obirgatório!")
        String lossReason
) {
}
