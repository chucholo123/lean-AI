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
@ConditionalOnExpression("'${os.name}'.toLowerCase().contains('windows')")
public class WindowsSystemAdapter implements SystemOperatorPort {

    @Override
    public ExecutionResult execute(SystemCommand command) {
        log.info("Ejecutando comando en Windows usando PowerShell: {}", command.action());

        try {
            ProcessBuilder builder = new ProcessBuilder();

            // Mapeamos las acciones puras de Java a comandos nativos de PowerShell
            switch (command.action()) {
                case OPEN_APPLICATION -> {
                    String appName = command.parameters().get("appName");
                    builder.command("powershell.exe", "-Command", "Start-Process '" + appName + "'");
                }
                case CREATE_DIRECTORY -> {
                    String path = command.parameters().get("path");
                    builder.command("powershell.exe", "-Command", "New-Item -ItemType Directory -Force -Path '" + path + "'");
                }
                default -> throw new UnsupportedOperationException("Acción no soportada en Windows");
            }

            Process process = builder.start();
            int exitCode = process.waitFor();

            String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            if (exitCode == 0) {
                return ExecutionResult.success("Comando ejecutado correctamente en Windows", output);
            } else {
                String errorOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                        .lines().collect(Collectors.joining("\n"));
                return ExecutionResult.failure("El comando de Windows falló. Detalles: " + errorOutput);
            }

        } catch (Exception e) {
            log.error("Error crítico ejecutando comando en Windows", e);
            return ExecutionResult.failure(e.getMessage());
        }
    }

    @Override
    public String readSystemMetrics() {
        try {
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command",
                    "[math]::Round((Get-CimInstance Win32_OperatingSystem).FreePhysicalMemory / 1024)");

            Process process = pb.start();
            String freeRam = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();

            return "RAM Libre en el sistema: " + freeRam + " MB";
        } catch (Exception e) {
            return "No se pudieron leer las métricas de Windows";
        }
    }
}