package com.mv.financeiro_controladoria.application.service;


import com.mv.financeiro_controladoria.application.dto.AccountDTO;
import com.mv.financeiro_controladoria.domain.model.Account;
import com.mv.financeiro_controladoria.domain.repository.AccountRepository;
import com.mv.financeiro_controladoria.domain.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    MovementRepository movementRepository;

    @InjectMocks
    AccountService service;

    private Account existing;

    @BeforeEach
    void setUp() {
        existing = new Account();
        existing.setActive(true);
        existing.setBank("Old Bank");
        existing.setNumber("0000-0");
    }

    @Test
    void update_deve_atualizar_quando_nao_tem_movimentos_e_conta_ativa() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(movementRepository.countByAccount_Id(10L)).thenReturn(0L);
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AccountDTO dto = new AccountDTO();
        dto.bank = "Nubank";
        dto.number = "12345-6";

        Account atualizado = service.update(10L, dto);

        assertEquals("Nubank", atualizado.getBank());
        assertEquals("12345-6", atualizado.getNumber());
        verify(accountRepository).save(existing);
    }

    @Test
    void update_deve_lancar_quando_ha_movimentos() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(movementRepository.countByAccount_Id(10L)).thenReturn(5L);

        AccountDTO dto = new AccountDTO();
        dto.bank = "Outro";
        dto.number = "9999-9";

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.update(10L, dto));

        assertTrue(ex.getMessage().contains("não pode ser alterada"));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void update_deve_lancar_quando_conta_inativa() {
        existing.setActive(false);
        when(accountRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(movementRepository.countByAccount_Id(10L)).thenReturn(0L);

        AccountDTO dto = new AccountDTO();
        dto.bank = "Outro";
        dto.number = "9999-9";

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.update(10L, dto));

        assertTrue(ex.getMessage().contains("Conta inativa"));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void update_deve_lancar_quando_nao_encontrada() {
        when(accountRepository.findById(10L)).thenReturn(Optional.empty());

        AccountDTO dto = new AccountDTO();
        dto.bank = "X";
        dto.number = "Y";

        assertThrows(IllegalArgumentException.class, () -> service.update(10L, dto));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void deleteLogical_deve_marcar_como_inativa_e_salvar() {
        when(accountRepository.findById(7L)).thenReturn(Optional.of(existing));

        service.deleteLogical(7L);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account salvo = captor.getValue();
        assertFalse(salvo.getActive(), "Conta deveria estar inativa após exclusão lógica");
    }

    @Test
    void deleteLogical_deve_lancar_quando_nao_encontrada() {
        when(accountRepository.findById(7L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.deleteLogical(7L));
        verify(accountRepository, never()).save(any());
    }
}
