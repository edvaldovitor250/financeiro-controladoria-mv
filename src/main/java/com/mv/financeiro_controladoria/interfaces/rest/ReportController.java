package com.mv.financeiro_controladoria.interfaces.rest;

import com.mv.financeiro_controladoria.application.dto.report.AllClientsBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.report.ClientBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.report.CompanyRevenueReportDTO;
import com.mv.financeiro_controladoria.application.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Relatórios de saldo e receita")
@Validated
public class ReportController {

    private final ReportService service;

    @Operation(summary = "Relatório de saldo do cliente em período")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/clients/{clientId}/balance")
    public ResponseEntity<ClientBalanceReportDTO> clientBalance(
            @PathVariable @Positive(message = "clientId deve ser positivo") Long clientId,
            @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate end) {

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start não pode ser maior que end.");
        }
        return ResponseEntity.ok(service.clientBalance(clientId, start, end));
    }

    @Operation(summary = "Relatório de receita da empresa por período")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping("/company/revenue")
    public ResponseEntity<CompanyRevenueReportDTO> companyRevenue(
            @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate end) {

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start não pode ser maior que end.");
        }
        return ResponseEntity.ok(service.companyRevenue(start, end));
    }

    @Operation(summary = "Relatório de saldo de todos os clientes em uma data")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Relatório gerado"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos") })
    @GetMapping("/clients/balances")
    public ResponseEntity<AllClientsBalanceReportDTO> allBalances(
            @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(service.allClientsBalanceAt(date));
    }
}
