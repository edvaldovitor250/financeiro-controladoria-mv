package com.mv.financeiro_controladoria.domain.services;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FeeCalculatorServiceTest {

    private final FeeCalculatorService service = new FeeCalculatorService();

    @Test
    void pricePerMovement_faixaAte10() {
        assertEquals(new BigDecimal("1.00"), service.pricePerMovement(0));
        assertEquals(new BigDecimal("1.00"), service.pricePerMovement(10));
    }

    @Test
    void pricePerMovement_faixa11a20() {
        assertEquals(new BigDecimal("0.75"), service.pricePerMovement(11));
        assertEquals(new BigDecimal("0.75"), service.pricePerMovement(20));
    }

    @Test
    void pricePerMovement_acima20() {
        assertEquals(new BigDecimal("0.50"), service.pricePerMovement(21));
        assertEquals(new BigDecimal("0.50"), service.pricePerMovement(100));
    }

    @Test
    void revenueFor_calculaCorretamente() {
        assertEquals(new BigDecimal("0.00"),  service.revenueFor(0));
        assertEquals(new BigDecimal("10.00"), service.revenueFor(10));
        assertEquals(new BigDecimal("8.25"),  service.revenueFor(11));
        assertEquals(new BigDecimal("10.00"), service.revenueFor(20));
        assertEquals(new BigDecimal("15.00"), service.revenueFor(20));
        assertEquals(new BigDecimal("25.00"), service.revenueFor(50));
    }
}