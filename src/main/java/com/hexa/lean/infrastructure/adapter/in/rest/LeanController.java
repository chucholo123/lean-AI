package com.hexa.lean.infrastructure.adapter.in.rest;

import com.hexa.lean.domain.model.ExecutionResult;
import com.hexa.lean.domain.port.in.ProcessUserIntentUseCase;
import com.hexa.lean.infrastructure.adapter.in.rest.dto.LeanRequest;
import com.hexa.lean.infrastructure.adapter.in.rest.dto.LeanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/lean")
@RequiredArgsConstructor
public class LeanController {

    private final ProcessUserIntentUseCase processUserIntentUseCase;

    @PostMapping("/process")
    public ResponseEntity<LeanResponse> processAudioText(@RequestBody LeanRequest request) {
        log.info("API Hit: Recibida nueva transcripción de audio: '{}'", request.message());

        if (request.message() == null || request.message().trim().isEmpty()) {
            log.warn("Mensaje vacío recibido");
            return ResponseEntity.badRequest()
                    .body(LeanResponse.builder()
                            .success(false)
                            .responseMessage("No pude escuchar nada.")
                            .build());
        }

        try {
            ExecutionResult result = processUserIntentUseCase.process(request.message());

            return ResponseEntity.ok(LeanResponse.fromDomain(result));

        } catch (Exception e) {
            log.error("Error procesando la intención de Lean", e);
            return ResponseEntity.internalServerError()
                    .body(LeanResponse.builder()
                            .success(false)
                            .responseMessage("Hubo un fallo en mi sistema: " + e.getMessage())
                            .build());
        }
    }
}