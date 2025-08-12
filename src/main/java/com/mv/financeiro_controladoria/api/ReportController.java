package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.dto.ClientBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.CompanyRevenueReportDTO;
import com.mv.financeiro_controladoria.application.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/clients/{clientId}/balance")
    public ResponseEntity<ClientBalanceReportDTO> clientBalance(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.clientBalance(clientId, start, end));
    }

    @GetMapping("/company/revenue")
    public ResponseEntity<CompanyRevenueReportDTO> companyRevenue(
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.companyRevenue(start, end));
    }
}
