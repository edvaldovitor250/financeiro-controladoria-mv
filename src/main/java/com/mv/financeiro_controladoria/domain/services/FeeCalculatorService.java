package com.mv.financeiro_controladoria.domain.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FeeCalculatorService {

    private static final BigDecimal PRICE_TIER1 = new BigDecimal("1.00");
    private static final BigDecimal PRICE_TIER2 = new BigDecimal("0.75");
    private static final BigDecimal PRICE_TIER3 = new BigDecimal("0.50");
    private static final RoundingMode MONEY_ROUND = RoundingMode.HALF_UP;

    public BigDecimal pricePerMovement(long totalMovements) {
        if (totalMovements < 0) {
            throw new IllegalArgumentException("totalMovements não pode ser negativo");
        }
        if (totalMovements <= 10) return PRICE_TIER1;
        if (totalMovements <= 20) return PRICE_TIER2;
        return PRICE_TIER3;
    }

    public BigDecimal revenueFor(long totalMovements) {
        if (totalMovements < 0) {
            throw new IllegalArgumentException("totalMovements não pode ser negativo");
        }
        if (totalMovements == 0) return BigDecimal.ZERO.setScale(2, MONEY_ROUND);

        return pricePerMovement(totalMovements)
                .multiply(BigDecimal.valueOf(totalMovements))
                .setScale(2, MONEY_ROUND);
    }
}
