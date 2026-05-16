package com.hexa.lean.infrastructure.adapter.in.rest.dto;

import lombok.Builder;

@Builder
public record LeanRequest(
        String message // El texto transcrito que vendrá del micrófono
) {}