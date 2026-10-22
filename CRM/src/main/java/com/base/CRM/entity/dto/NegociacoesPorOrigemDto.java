package com.base.CRM.entity.dto;

import com.base.CRM.enumClass.Origin;

public record NegociacoesPorOrigemDto(
    Origin origin,
    Long quantidade
) {}