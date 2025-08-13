package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import com.mv.financeiro_controladoria.domain.services.FeeCalculatorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class BillingService {

    private  MovementRepository movRepo;
    private  ClientRepository clientRepo;
    private  FeeCalculatorService fees;

    public BigDecimal feeForClientOn30DayCycles(Long clientId, LocalDate start, LocalDate end) {
        Client c = clientRepo.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        LocalDate anchor = c.getCreatedAt() != null ? c.getCreatedAt() : start;

        LocalDate cursor = start.isAfter(anchor) ? start : anchor;
        BigDecimal total = BigDecimal.ZERO;

        while (!cursor.isAfter(end)) {
            LocalDate cycleEndExclusive = cursor.plusDays(30);
            LocalDate queryEnd = cycleEndExclusive.minusDays(1);
            if (queryEnd.isAfter(end)) queryEnd = end;
            if (queryEnd.isBefore(cursor)) break;

            long count = movRepo.countByClientAndPeriod(c.getId(), cursor, queryEnd);
            total = total.add(fees.revenueFor(count));

            cursor = cycleEndExclusive;
        }
        return total.setScale(2);
    }
}
