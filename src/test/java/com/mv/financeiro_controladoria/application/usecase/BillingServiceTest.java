package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.domain.services.FeeCalculatorService;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillingService – cálculo de tarifas por ciclos de 30 dias")
class BillingServiceTest {

    @Mock MovementRepository movRepo;
    @Mock ClientRepository clientRepo;

    @Spy  FeeCalculatorService feeCalc = new FeeCalculatorService();

    @InjectMocks BillingService billing;

    private Client client;

    @BeforeEach
    @DisplayName("Setup: cliente criado em 10/01/2025")
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("Cliente A");
        client.setCreatedAt(LocalDate.of(2025, 1, 10));
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
    }

    @Test
    @DisplayName("Deve somar duas janelas de 30 dias dentro do período informado")
    void feeForClientOn30DayCycles_duasJanelas() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end   = LocalDate.of(2025, 2, 15);

        when(movRepo.countByClientAndPeriod(
                eq(1L),
                eq(LocalDate.of(2025, 1, 10)),
                eq(LocalDate.of(2025, 2, 8))
        )).thenReturn(5L);

        when(movRepo.countByClientAndPeriod(
                eq(1L),
                eq(LocalDate.of(2025, 2, 9)),
                eq(LocalDate.of(2025, 2, 15))
        )).thenReturn(12L);

        BigDecimal total = billing.feeForClientOn30DayCycles(1L, start, end);

        assertEquals(new BigDecimal("14.00"), total);
    }

    @Test
    @DisplayName("Quando o período termina antes do anchor (createdAt), total deve ser 0.00")
    void feeForClientOn30DayCycles_periodoAntesDoCadastro() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end   = LocalDate.of(2025, 1, 5);

        BigDecimal total = billing.feeForClientOn30DayCycles(1L, start, end);

        assertEquals(new BigDecimal("0.00"), total);
        Mockito.verifyNoInteractions(movRepo);
    }

    @Test
    @DisplayName("Quando o período cabe em uma única janela, calcula apenas essa janela")
    void feeForClientOn30DayCycles_umaJanela() {
        LocalDate start = LocalDate.of(2025, 1, 12);
        LocalDate end   = LocalDate.of(2025, 1, 25);

        when(movRepo.countByClientAndPeriod(
                eq(1L),
                eq(LocalDate.of(2025, 1, 12)),
                eq(LocalDate.of(2025, 1, 25))
        )).thenReturn(21L);

        BigDecimal total = billing.feeForClientOn30DayCycles(1L, start, end);

        assertEquals(new BigDecimal("10.50"), total);
    }
}
