package com.mv.financeiro_controladoria.interfaces.rest;

import com.mv.financeiro_controladoria.infra.db.CompanyRevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@Tag(name = "Company", description = "Cálculos de receita (PL/SQL)")
public class CompanyController {

    private final CompanyRevenueService revenueService;

    @Operation(summary = "Saldo líquido do cliente (PL/SQL)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo retornado"),
            @ApiResponse(responseCode = "400", description = "Cliente inválido")
    })
    @GetMapping("/clients/{clientId}/net-balance")
    public ResponseEntity<BigDecimal> getClientNetBalance(@PathVariable Long clientId) {
        return ResponseEntity.ok(revenueService.getClientNetBalance(clientId));
    }

    @Operation(summary = "Receita da empresa no período (PL/SQL)")
    @ApiResponse(responseCode = "200", description = "Receita total do período")
    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> revenue(
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(revenueService.revenue(start, end));
    }
}
