package com.mv.financeiro_controladoria.application.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(name = "ErrorResponse", description = "Envelope de erro padrão da API")
public class ErrorResponse {
    @Schema(example = "2025-08-12T16:40:10Z")
    public OffsetDateTime timestamp;
    @Schema(example = "/api/clients/999")
    public String path;
    @Schema(example = "404")
    public int status;
    @Schema(example = "Not Found")
    public String error;
    @Schema(example = "Cliente não encontrado")
    public String message;

    public static ErrorResponse of(int status, String error, String message, String path) {
        ErrorResponse e = new ErrorResponse();
        e.timestamp = OffsetDateTime.now();
        e.status = status;
        e.error = error;
        e.message = message;
        e.path = path;
        return e;
    }
}
