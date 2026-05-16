package com.hexa.lean.infrastructure.adapter.out.system;

import com.hexa.lean.domain.model.ExecutionResult;
import com.hexa.lean.domain.model.SystemCommand;
import com.hexa.lean.domain.port.out.SystemOperatorPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnExpression("'${os.name}'.toLowerCase().contains('linux')")
public class LinuxSystemAdapter implements SystemOperatorPort {
    @Override
    public ExecutionResult execute(SystemCommand command) {
        log.info("Ejecutando comando en Arch Linux: {}", command.action());

        try {
            ProcessBuilder builder = new ProcessBuilder();

            // Un switch para mapear las acciones a comandos Bash
            switch (command.action()) {
                case OPEN_APPLICATION -> builder.command("bash", "-c", command.parameters().get("appName"));
                case CREATE_DIRECTORY -> builder.command("bash", "-c", "mkdir -p " + command.parameters().get("path"));
                default -> throw new UnsupportedOperationException("Acción no soportada en Linux");
            }

            Process process = builder.start();
            int exitCode = process.waitFor();

            String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            if (exitCode == 0) {
                return ExecutionResult.success("Comando ejecutado correctamente", output);
            } else {
                return ExecutionResult.failure("El comando falló con código: " + exitCode);
            }

        } catch (Exception e) {
            log.error("Error crítico ejecutando comando", e);
            return ExecutionResult.failure(e.getMessage());
        }
    }

    @Override
    public String readSystemMetrics() {
        return "RAM Libre: 4096MB"; // Simulado por brevedad
    }
}