package com.hexa.lean.infrastructure.adapter.in.rest.dto;

import com.hexa.lean.domain.model.ExecutionResult;
import lombok.Builder;

@Builder
public record LeanResponse(
        boolean success,
        String responseMessage,
        String terminalOutput
) {

    public static LeanResponse fromDomain(ExecutionResult result) {
        return LeanResponse.builder()
                .success(result.isSuccessful())
                .responseMessage(result.message())
                .terminalOutput(result.rawOutput())
                .build();
    }
}