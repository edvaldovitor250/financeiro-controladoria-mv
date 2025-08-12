package com.mv.financeiro_controladoria.application.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FeeCalculatorTest {

    @Test
    void fee_ate_10() {
        FeeCalculator f = new FeeCalculator();
        assertEquals(new BigDecimal("10.00"), f.revenueFor(10));
    }

    @Test void fee_11_a_20() {
        FeeCalculator f = new FeeCalculator();
        assertEquals(new BigDecimal("8.25"), f.revenueFor(11));
    }

    @Test void fee_acima_20() {
        FeeCalculator f = new FeeCalculator();
        assertEquals(new BigDecimal("10.50"), f.revenueFor(21));
    }
}
