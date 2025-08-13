package com.mv.financeiro_controladoria.interfaces.rest;

import com.mv.financeiro_controladoria.infra.db.CompanyRevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@Tag(name = "Company", description = "Cálculos de receita (PL/SQL)")
@Validated
public class CompanyController {

    private final CompanyRevenueService revenueService;

    @Operation(summary = "Saldo líquido do cliente (PL/SQL)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo retornado"),
            @ApiResponse(responseCode = "400", description = "Parâmetro inválido"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/clients/{clientId}/net-balance")
    public ResponseEntity<BigDecimal> getClientNetBalance(
            @PathVariable @Positive(message = "clientId deve ser positivo") Long clientId) {

        BigDecimal balance = revenueService.getClientNetBalance(clientId);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "Receita da empresa no período (PL/SQL)")
    @ApiResponse(responseCode = "200", description = "Receita total do período")
    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> revenue(
            @Parameter(description = "Data inicial", examples = {
                    @ExampleObject(name = "início do mês", value = "2025-08-01")
            })
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate start,

            @Parameter(description = "Data final", examples = {
                    @ExampleObject(name = "fim do mês", value = "2025-08-31")
            })
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("Parâmetros 'start' e 'end' são obrigatórios.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Parâmetro 'start' não pode ser maior que 'end'.");
        }

        BigDecimal total = revenueService.revenue(start, end);
        return ResponseEntity.ok(total);
    }
}
