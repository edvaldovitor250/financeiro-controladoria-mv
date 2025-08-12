package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.service.CompanyRevenueService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyRevenueService revenueService;

    public CompanyController(CompanyRevenueService revenueService) {
        this.revenueService = revenueService;
    }

    // PL/SQL: saldo líquido do cliente
    @GetMapping("/clients/{clientId}/net-balance")
    public ResponseEntity<BigDecimal> getClientNetBalance(@PathVariable Long clientId) {
        return ResponseEntity.ok(revenueService.getClientNetBalance(clientId));
    }

    // PL/SQL: receita da empresa no período
    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> revenue(
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(revenueService.revenue(start, end));
    }
}
