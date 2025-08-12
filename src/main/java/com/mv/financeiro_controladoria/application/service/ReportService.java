package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.ClientBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.CompanyRevenueReportDTO;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
import com.mv.financeiro_controladoria.domain.repository.MovementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final ClientRepository clientRepo;
    private final MovementRepository movRepo;
    private final FeeCalculator fees = new FeeCalculator();

    public ReportService(ClientRepository c, MovementRepository m) {
        this.clientRepo = c; this.movRepo = m;
    }

    public ClientBalanceReportDTO clientBalance(Long clientId, LocalDate start, LocalDate end) {
        Client c = clientRepo.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        BigDecimal credit = movRepo.sumCreditByClientAndPeriod(clientId, start, end);
        BigDecimal debit  = movRepo.sumDebitByClientAndPeriod(clientId, start, end);
        long total = movRepo.countByClientAndPeriod(clientId, start, end);
        BigDecimal fee = fees.revenueFor(total);
        BigDecimal initial = initialBalance(clientId);
        BigDecimal current = initial.add(credit).subtract(debit);
        return ClientBalanceReportDTO.of(c, credit, debit, total, fee, initial, current, start, end);
    }

    public CompanyRevenueReportDTO companyRevenue(LocalDate start, LocalDate end) {
        List<Client> clients = clientRepo.findAll();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        List<CompanyRevenueReportDTO.Item> items = new ArrayList<>();

        for (Client c : clients) {
            long total = movRepo.countByClientAndPeriod(c.getId(), start, end);
            BigDecimal fee = fees.revenueFor(total);
            items.add(new CompanyRevenueReportDTO.Item(c.getId(), c.getName(), total, fee));
            totalRevenue = totalRevenue.add(fee);
        }
        return new CompanyRevenueReportDTO(start, end, items, totalRevenue);
    }

    private BigDecimal initialBalance(Long clientId) {
        return BigDecimal.ZERO;
    }
}
