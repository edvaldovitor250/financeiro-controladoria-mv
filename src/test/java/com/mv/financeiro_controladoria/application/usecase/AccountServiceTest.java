package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.application.dto.account.AccountDTO;
import com.mv.financeiro_controladoria.domain.entity.Account;
import com.mv.financeiro_controladoria.infra.persistence.repository.AccountRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @Mock MovementRepository movementRepository;

    @InjectMocks AccountService service;

    @Test
    void update_ok_quandoSemMovimentacaoEAtiva() {
        Account acc = new Account();
        acc.setId(1L);
        acc.setActive(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));
        when(movementRepository.countByAccount_Id(1L)).thenReturn(0L);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        AccountDTO dto = new AccountDTO();
        dto.bank = "Itau";
        dto.number = "1234-5";

        Account updated = service.update(1L, dto);
        assertEquals("Itau", updated.getBank());
        assertEquals("1234-5", updated.getNumber());
    }

    @Test
    void update_bloqueia_quandoHaMovimentacao() {
        Account acc = new Account(); acc.setId(2L); acc.setActive(true);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(acc));
        when(movementRepository.countByAccount_Id(2L)).thenReturn(3L);

        AccountDTO dto = new AccountDTO(); dto.bank = "BB"; dto.number = "9999";

        assertThrows(IllegalStateException.class, () -> service.update(2L, dto));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void update_bloqueia_quandoInativa() {
        Account acc = new Account(); acc.setId(3L); acc.setActive(false);
        when(accountRepository.findById(3L)).thenReturn(Optional.of(acc));
        when(movementRepository.countByAccount_Id(3L)).thenReturn(0L);

        AccountDTO dto = new AccountDTO(); dto.bank = "BB"; dto.number = "9999";

        assertThrows(IllegalStateException.class, () -> service.update(3L, dto));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void deleteLogical_deveInativar() {
        Account acc = new Account(); acc.setId(10L); acc.setActive(true);
        when(accountRepository.findById(10L)).thenReturn(Optional.of(acc));

        service.deleteLogical(10L);

        assertFalse(acc.getActive());
        verify(accountRepository).save(acc);
    }
}
