package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.report.AllClientsBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.report.ClientBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.report.CompanyRevenueReportDTO;
import com.mv.financeiro_controladoria.application.usecase.BillingService;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ClientRepository clientRepo;
    private final MovementRepository movRepo;
    private final BillingService billingService;

    public ClientBalanceReportDTO clientBalance(Long clientId, LocalDate start, LocalDate end) {
        Client c = clientRepo.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        BigDecimal credit = movRepo.sumCreditByClientAndPeriod(clientId, start, end);
        BigDecimal debit  = movRepo.sumDebitByClientAndPeriod(clientId, start, end);
        long total        = movRepo.countByClientAndPeriod(clientId, start, end);

        BigDecimal fee    = billingService.feeForClientOn30DayCycles(clientId, start, end);

        BigDecimal initial = initialBalanceAt(clientId, start);
        BigDecimal current = initial.add(credit).subtract(debit);

        return ClientBalanceReportDTO.of(c, credit, debit, total, fee, initial, current, start, end);
    }

    public CompanyRevenueReportDTO companyRevenue(LocalDate start, LocalDate end) {
        List<Client> clients = clientRepo.findAll();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        List<CompanyRevenueReportDTO.Item> items = new ArrayList<>();

        for (Client c : clients) {
            long totalMovs = movRepo.countByClientAndPeriod(c.getId(), start, end);

            BigDecimal fee = billingService.feeForClientOn30DayCycles(c.getId(), start, end);

            items.add(new CompanyRevenueReportDTO.Item(c.getId(), c.getName(), totalMovs, fee));
            totalRevenue = totalRevenue.add(fee);
        }
        return new CompanyRevenueReportDTO(start, end, items, totalRevenue);
    }

    public AllClientsBalanceReportDTO allClientsBalanceAt(LocalDate date) {
        List<Client> clients = clientRepo.findAll();
        List<AllClientsBalanceReportDTO.Item> items = new ArrayList<>();
        for (Client c : clients) {
            BigDecimal credit = movRepo.sumCreditByClientUntil(c.getId(), date);
            BigDecimal debit  = movRepo.sumDebitByClientUntil(c.getId(), date);
            BigDecimal bal    = credit.subtract(debit);
            items.add(new AllClientsBalanceReportDTO.Item(c.getId(), c.getName(), c.getCreatedAt(), bal));
        }
        return new AllClientsBalanceReportDTO(date, items);
    }

    private BigDecimal initialBalanceAt(Long clientId, LocalDate start) {
        LocalDate ref = start.minusDays(1);
        BigDecimal credit = movRepo.sumCreditByClientUntil(clientId, ref);
        BigDecimal debit  = movRepo.sumDebitByClientUntil(clientId, ref);
        return credit.subtract(debit);
    }
}
