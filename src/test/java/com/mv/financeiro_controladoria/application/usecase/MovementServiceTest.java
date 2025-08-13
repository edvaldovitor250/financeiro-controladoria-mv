package com.mv.financeiro_controladoria.application.usecase;


import com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO;
import com.mv.financeiro_controladoria.domain.entity.Account;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.domain.entity.Movement;
import com.mv.financeiro_controladoria.infra.persistence.repository.AccountRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock MovementRepository movementRepository;
    @Mock AccountRepository accountRepository;

    @InjectMocks MovementService service;

    @Test
    void createForClient_deveSalvarComContaETipoDataInformados() {
        Client client = new Client(); client.setId(10L);
        Account acc = new Account(); acc.setId(99L);

        MovementCreateDTO dto = new MovementCreateDTO();
        dto.type = "RECEITA";
        dto.amount = new BigDecimal("123.45");
        dto.description = "depósito";
        dto.date = LocalDate.of(2025, 1, 31);
        dto.accountId = 99L;

        when(accountRepository.findById(99L)).thenReturn(Optional.of(acc));
        ArgumentCaptor<Movement> captor = ArgumentCaptor.forClass(Movement.class);
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArgument(0));

        Movement saved = service.createForClient(client, dto);

        verify(movementRepository).save(captor.capture());
        Movement m = captor.getValue();

        assertEquals(client, m.getClient());
        assertEquals(acc, m.getAccount());
        assertEquals(dto.amount, m.getAmount());
        assertEquals(dto.description, m.getDescription());
        assertEquals(dto.date, m.getDate());
        assertEquals("RECEITA", m.getType().name());
        assertSame(saved, m);
    }

    @Test
    void createForClient_quandoContaNaoExiste_disparaExcecao() {
        Client client = new Client(); client.setId(10L);
        MovementCreateDTO dto = new MovementCreateDTO();
        dto.type = "DESPESA";
        dto.amount = new BigDecimal("10");
        dto.accountId = 999L;

        when(accountRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.createForClient(client, dto));
        verify(movementRepository, never()).save(any());
    }
}