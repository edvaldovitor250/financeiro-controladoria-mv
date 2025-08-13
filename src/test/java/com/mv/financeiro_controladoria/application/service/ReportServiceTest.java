package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.report.AllClientsBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.report.ClientBalanceReportDTO;
import com.mv.financeiro_controladoria.application.dto.report.CompanyRevenueReportDTO;
import com.mv.financeiro_controladoria.application.usecase.BillingService;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock ClientRepository clientRepo;
    @Mock MovementRepository movRepo;
    @Mock BillingService billing;

    @InjectMocks ReportService service;

    @Test
    void clientBalance_ok() {
        Long clientId = 1L;
        LocalDate start = LocalDate.of(2025, 1, 10);
        LocalDate end   = LocalDate.of(2025, 1, 31);

        Client c = new Client();
        c.setId(clientId);
        c.setName("Ana");
        c.setCreatedAt(LocalDate.of(2025, 1, 1));
        when(clientRepo.findById(clientId)).thenReturn(Optional.of(c));

        when(movRepo.sumCreditByClientAndPeriod(clientId, start, end)).thenReturn(new BigDecimal("300.00"));
        when(movRepo.sumDebitByClientAndPeriod(clientId, start, end)).thenReturn(new BigDecimal("180.00"));
        when(movRepo.countByClientAndPeriod(clientId, start, end)).thenReturn(15L);

        when(billing.feeForClientOn30DayCycles(clientId, start, end)).thenReturn(new BigDecimal("11.25"));

        when(movRepo.sumCreditByClientUntil(clientId, start.minusDays(1))).thenReturn(new BigDecimal("100.00"));
        when(movRepo.sumDebitByClientUntil(clientId, start.minusDays(1))).thenReturn(new BigDecimal("40.00"));

        ClientBalanceReportDTO dto = service.clientBalance(clientId, start, end);

        assertEquals(clientId, dto.clientId);
        assertEquals("Ana", dto.clientName);
        assertEquals(LocalDate.of(2025, 1, 1), dto.clientSince);
        assertEquals(15L, dto.totalCount);
        assertEquals(new BigDecimal("11.25"), dto.feePaid);
        assertEquals(new BigDecimal("60.00"), dto.initialBalance);
        assertEquals(new BigDecimal("180.00"), dto.currentBalance);
        assertEquals(start, dto.start);
        assertEquals(end, dto.end);
    }

    @Test
    void companyRevenue_ok() {
        Client a = new Client(); a.setId(1L); a.setName("A");
        Client b = new Client(); b.setId(2L); b.setName("B");
        when(clientRepo.findAll()).thenReturn(Arrays.asList(a, b));

        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end   = LocalDate.of(2025, 1, 31);

        when(movRepo.countByClientAndPeriod(1L, start, end)).thenReturn(12L);
        when(movRepo.countByClientAndPeriod(2L, start, end)).thenReturn(5L);

        when(billing.feeForClientOn30DayCycles(1L, start, end)).thenReturn(new BigDecimal("9.00"));
        when(billing.feeForClientOn30DayCycles(2L, start, end)).thenReturn(new BigDecimal("5.00"));

        CompanyRevenueReportDTO rep = service.companyRevenue(start, end);

        assertEquals(start, rep.start);
        assertEquals(end, rep.end);
        assertEquals(new BigDecimal("14.00"), rep.total);
        assertEquals(2, rep.clients.size());

        assertEquals(1L, rep.clients.get(0).clientId);
        assertEquals("A", rep.clients.get(0).clientName);
        assertEquals(12L, rep.clients.get(0).movementCount);
        assertEquals(new BigDecimal("9.00"), rep.clients.get(0).amount);

        assertEquals(2L, rep.clients.get(1).clientId);
        assertEquals("B", rep.clients.get(1).clientName);
        assertEquals(5L, rep.clients.get(1).movementCount);
        assertEquals(new BigDecimal("5.00"), rep.clients.get(1).amount);
    }

    @Test
    void allClientsBalanceAt_ok() {
        LocalDate ref = LocalDate.of(2025, 1, 31);
        Client a = new Client(); a.setId(1L); a.setName("A"); a.setCreatedAt(LocalDate.of(2025, 1, 1));
        Client b = new Client(); b.setId(2L); b.setName("B"); b.setCreatedAt(LocalDate.of(2025, 1, 5));
        when(clientRepo.findAll()).thenReturn(Arrays.asList(a, b));

        when(movRepo.sumCreditByClientUntil(1L, ref)).thenReturn(new BigDecimal("150"));
        when(movRepo.sumDebitByClientUntil(1L, ref)).thenReturn(new BigDecimal("30"));
        when(movRepo.sumCreditByClientUntil(2L, ref)).thenReturn(new BigDecimal("80"));
        when(movRepo.sumDebitByClientUntil(2L, ref)).thenReturn(new BigDecimal("20"));

        AllClientsBalanceReportDTO dto = service.allClientsBalanceAt(ref);

        assertEquals(ref, dto.date);
        assertEquals(2, dto.items.size());
    }
}
