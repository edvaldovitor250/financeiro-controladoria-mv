package com.mv.financeiro_controladoria.application.usecase;


import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.domain.services.FeeCalculatorService;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock MovementRepository movRepo;
    @Mock ClientRepository clientRepo;
    @Spy  FeeCalculatorService feeCalc = new FeeCalculatorService();

    @InjectMocks BillingService billing;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("Cliente A");
        client.setCreatedAt(LocalDate.of(2025, 1, 10));
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    }

    @Test
    void feeForClientOn30DayCycles_duasJanelas() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end   = LocalDate.of(2025, 2, 15);

        when(movRepo.countByClientAndPeriod(eq(1L),
                eq(LocalDate.of(2025,1,10)), eq(LocalDate.of(2025,2,8))))
                .thenReturn(5L);
        when(movRepo.countByClientAndPeriod(eq(1L),
                eq(LocalDate.of(2025,2,9)),  eq(LocalDate.of(2025,2,15))))
                .thenReturn(12L);

        BigDecimal total = billing.feeForClientOn30DayCycles(1L, start, end);
        assertEquals(new BigDecimal("14.00"), total);
    }
}