package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.dto.ClientBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.CompanyRevenueReportDTO;
import com.mv.financeiro_controladoria.application.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Relatórios de saldo e receita")
public class ReportController {

    private final ReportService service;

    @Operation(summary = "Relatório de saldo do cliente em período")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado"),
            @ApiResponse(responseCode = "400", description = "Cliente inválido")
    })
    @GetMapping("/clients/{clientId}/balance")
    public ResponseEntity<ClientBalanceReportDTO> clientBalance(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.clientBalance(clientId, start, end));
    }

    @Operation(summary = "Relatório de receita da empresa por período")
    @ApiResponse(responseCode = "200", description = "Relatório gerado")
    @GetMapping("/company/revenue")
    public ResponseEntity<CompanyRevenueReportDTO> companyRevenue(
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.companyRevenue(start, end));
    }
}
