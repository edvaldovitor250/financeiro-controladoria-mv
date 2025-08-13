package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.application.dto.account.AccountCreateDTO;
import com.mv.financeiro_controladoria.application.dto.account.AccountResponseDTO;
import com.mv.financeiro_controladoria.application.dto.account.AccountUpdateDTO;
import com.mv.financeiro_controladoria.domain.entity.Account;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.infra.persistence.repository.AccountRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private MovementRepository movementRepository;

    @InjectMocks
    private AccountService service;

    private Client client;
    private Account account;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("João");

        account = new Account();
        account.setId(10L);
        account.setClient(client);
        account.setBank("Banco A");
        account.setNumber("0001");
        account.setActive(true);
    }

    @Test
    @DisplayName("create: deve criar conta para cliente existente e retornar DTO")
    void create_ok() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account acc = inv.getArgument(0);
            acc.setId(10L);
            return acc;
        });

        AccountCreateDTO dto = new AccountCreateDTO();
        dto.bank = "Banco XPTO";
        dto.number = "1234";

        AccountResponseDTO resp = service.create(1L, dto);

        ArgumentCaptor<Account> accCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accCaptor.capture());

        Account saved = accCaptor.getValue();
        assertThat(saved.getClient()).isEqualTo(client);
        assertThat(saved.getBank()).isEqualTo("Banco XPTO");
        assertThat(saved.getNumber()).isEqualTo("1234");
        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getBank()).isEqualTo("Banco XPTO");
        assertThat(resp.getNumber()).isEqualTo("1234");
        assertThat(resp.getClientId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("create: deve lançar 404 quando cliente não existe")
    void create_clientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.bank = "B";
        dto.number = "1";

        assertThatThrownBy(() -> service.create(99L, dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: sucesso quando conta ativa e sem movimentações")
    void update_ok() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(movementRepository.countByAccount_Id(10L)).thenReturn(0L);

        AccountUpdateDTO dto = new AccountUpdateDTO();
        dto.bank = "Banco B";
        dto.number = "9999";

        AccountResponseDTO resp = service.update(10L, dto);

        assertThat(account.getBank()).isEqualTo("Banco B");
        assertThat(account.getNumber()).isEqualTo("9999");
        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getBank()).isEqualTo("Banco B");
        assertThat(resp.getNumber()).isEqualTo("9999");
    }

    @Test
    @DisplayName("update: deve falhar se conta estiver inativa")
    void update_inactive() {
        account.setActive(false);
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        AccountUpdateDTO dto = new AccountUpdateDTO();
        dto.bank = "Banco C";
        dto.number = "2222";

        assertThatThrownBy(() -> service.update(10L, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Conta inativa não pode ser alterada");
        verify(movementRepository, never()).countByAccount_Id(anyLong());
    }

    @Test
    @DisplayName("update: deve falhar se conta tiver movimentações")
    void update_withMovements() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(movementRepository.countByAccount_Id(10L)).thenReturn(3L);

        AccountUpdateDTO dto = new AccountUpdateDTO();
        dto.bank = "Banco D";
        dto.number = "3333";

        assertThatThrownBy(() -> service.update(10L, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Conta com movimentações não pode ser alterada");
    }

    @Test
    @DisplayName("deleteLogical: deve marcar como inativa")
    void deleteLogical_ok() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        service.deleteLogical(10L);

        assertThat(account.getActive()).isFalse();
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteLogical: no-op se já estiver inativa")
    void deleteLogical_noop() {
        account.setActive(false);
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        service.deleteLogical(10L);

        assertThat(account.getActive()).isFalse();
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("listByClient: retorna lista mapeada")
    void listByClient_ok() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Account a1 = new Account();
        a1.setId(10L); a1.setClient(client); a1.setBank("B1"); a1.setNumber("N1"); a1.setActive(true);

        Account a2 = new Account();
        a2.setId(11L); a2.setClient(client); a2.setBank("B2"); a2.setNumber("N2"); a2.setActive(true);

        when(accountRepository.findByClientId(1L)).thenReturn(Arrays.asList(a1, a2));

        List<AccountResponseDTO> list = service.listByClient(1L);

        assertThat(list).hasSize(2);
        assertThat(list).extracting(AccountResponseDTO::getId).containsExactly(10L, 11L);
        assertThat(list).extracting(AccountResponseDTO::getBank).containsExactly("B1", "B2");
        assertThat(list).extracting(AccountResponseDTO::getNumber).containsExactly("N1", "N2");
    }

    @Test
    @DisplayName("get: retorna DTO quando existe")
    void get_ok() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        AccountResponseDTO dto = service.get(10L);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getBank()).isEqualTo("Banco A");
        assertThat(dto.getNumber()).isEqualTo("0001");
        assertThat(dto.getClientId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("get: lança 404 quando não existe")
    void get_notFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Conta não encontrada");
    }
}
