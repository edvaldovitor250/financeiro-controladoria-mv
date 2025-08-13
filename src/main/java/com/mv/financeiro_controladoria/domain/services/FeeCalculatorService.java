package com.mv.financeiro_controladoria.domain.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class FeeCalculatorService {
    public BigDecimal pricePerMovement(long totalMovements) {
        if (totalMovements <= 10) return new BigDecimal("1.00");
        if (totalMovements <= 20) return new BigDecimal("0.75");
        return new BigDecimal("0.50");
    }

    public BigDecimal revenueFor(long totalMovements) {
        return pricePerMovement(totalMovements)
                .multiply(BigDecimal.valueOf(totalMovements))
                .setScale(2);
    }
}
