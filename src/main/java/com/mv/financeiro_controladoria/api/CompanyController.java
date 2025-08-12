package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.service.CompanyRevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyRevenueService revenueService;

    public CompanyController(CompanyRevenueService revenueService) {
        this.revenueService = revenueService;
    }

    // Exemplo: consultar saldo líquido do cliente calculado via PL/SQL
    @GetMapping("/clients/{clientId}/net-balance")
    public ResponseEntity<Number> getClientNetBalance(@PathVariable Long clientId) {
        Number value = revenueService.getClientNetBalance(clientId);
        return ResponseEntity.ok(value);
    }
}
