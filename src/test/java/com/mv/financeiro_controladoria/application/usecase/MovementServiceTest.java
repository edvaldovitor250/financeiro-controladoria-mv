package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO;
import com.mv.financeiro_controladoria.application.dto.movement.MovementResponseDTO;
import com.mv.financeiro_controladoria.domain.entity.Account;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.domain.entity.Movement;
import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
import com.mv.financeiro_controladoria.infra.persistence.repository.AccountRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock MovementRepository movementRepository;
    @Mock AccountRepository accountRepository;
    @Mock ClientRepository clientRepository;

    @InjectMocks MovementService service;

    private Client client;
    private Account account;

    @BeforeEach
    void setup() {
        client = new Client();
        client.setId(1L);
        client.setName("Cliente A");

        account = new Account();
        account.setId(10L);
        account.setClient(client);
        account.setActive(true);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    }

    @Nested
    @DisplayName("createForClient")
    class CreateForClientTests {

        @Test
        @DisplayName("deve criar movimentação com conta, método de pagamento e amount em escala 2")
        void create_ok_withAccount() {
            MovementCreateDTO dto = new MovementCreateDTO();
            dto.setType(MovementType.RECEITA);
            dto.setAmount(new BigDecimal("123.4")); // deve virar 123.40
            dto.setDescription("Depósito");
            dto.setDate(LocalDate.of(2025, 8, 13));
            dto.setAccountId(10L);

            when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
            ArgumentCaptor<Movement> movCaptor = ArgumentCaptor.forClass(Movement.class);
            when(movementRepository.save(movCaptor.capture())).thenAnswer(inv -> {
                Movement m = inv.getArgument(0, Movement.class);
                m.setId(999L);
                return m;
            });

            MovementResponseDTO resp = service.createForClient(1L, dto);

            Movement saved = movCaptor.getValue();
            assertEquals(client.getId(), saved.getClient().getId());
            assertEquals(MovementType.RECEITA, saved.getType());
            assertEquals(new BigDecimal("123.40"), saved.getAmount());
            assertEquals("Depósito", saved.getDescription());
            assertEquals(LocalDate.of(2025, 8, 13), saved.getDate());
            assertNotNull(saved.getAccount());
            assertEquals(10L, saved.getAccount().getId());

            assertNotNull(resp);
            assertEquals(999L, resp.getId());
            assertEquals("RECEITA", resp.getType());
            assertEquals(new BigDecimal("123.40"), resp.getAmount());
        }

        @Test
        @DisplayName("deve lançar erro quando conta não pertence ao cliente")
        void create_fail_accountNotBelongsToClient() {
            Client other = new Client();
            other.setId(2L);
            Account acc = new Account();
            acc.setId(10L);
            acc.setClient(other);
            acc.setActive(true);

            MovementCreateDTO dto = new MovementCreateDTO();
            dto.setType(MovementType.DESPESA);
            dto.setAmount(new BigDecimal("50.00"));
            dto.setAccountId(10L);

            when(accountRepository.findById(10L)).thenReturn(Optional.of(acc));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.createForClient(1L, dto));
            assertTrue(ex.getMessage().contains("Conta não pertence ao cliente"));
            verify(movementRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar erro quando conta está inativa")
        void create_fail_inactiveAccount() {
            account.setActive(false);
            MovementCreateDTO dto = new MovementCreateDTO();
            dto.setType(MovementType.RECEITA);
            dto.setAmount(new BigDecimal("10.00"));
            dto.setAccountId(10L);

            when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.createForClient(1L, dto));
            assertTrue(ex.getMessage().contains("Conta está inativa"));
            verify(movementRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar 404 quando cliente não existe")
        void create_fail_clientNotFound() {
            when(clientRepository.findById(99L)).thenReturn(Optional.empty());
            MovementCreateDTO dto = new MovementCreateDTO();
            dto.setType(MovementType.RECEITA);
            dto.setAmount(new BigDecimal("10.00"));

            assertThrows(EntityNotFoundException.class,
                    () -> service.createForClient(99L, dto));
            verify(movementRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listByClient")
    class ListByClientTests {

        @Test
        @DisplayName("deve listar por cliente sem filtros")
        void list_noFilters() {
            Movement m1 = new Movement(); m1.setId(1L); m1.setType(MovementType.RECEITA); m1.setAmount(new BigDecimal("10.00")); m1.setDate(LocalDate.now());
            Movement m2 = new Movement(); m2.setId(2L); m2.setType(MovementType.DESPESA); m2.setAmount(new BigDecimal("5.00"));  m2.setDate(LocalDate.now());

            when(clientRepository.existsById(1L)).thenReturn(true);
            when(movementRepository.findByClientId(1L)).thenReturn(Arrays.asList(m1, m2));

            assertEquals(2, service.listByClient(1L, null, null, null).size());
        }

        @Test
        @DisplayName("deve listar por cliente + período")
        void list_periodOnly() {
            LocalDate start = LocalDate.of(2025, 8, 1);
            LocalDate end   = LocalDate.of(2025, 8, 31);

            Movement m = new Movement(); m.setId(3L); m.setType(MovementType.RECEITA); m.setAmount(new BigDecimal("20.00")); m.setDate(LocalDate.of(2025,8,10));

            when(clientRepository.existsById(1L)).thenReturn(true);
            when(movementRepository.findByClientIdAndDateBetween(1L, start, end))
                    .thenReturn(Collections.singletonList(m));

            assertEquals(1, service.listByClient(1L, start, end, null).size());
        }

        @Test
        @DisplayName("deve listar por cliente + tipo")
        void list_typeOnly() {
            Movement m = new Movement(); m.setId(4L); m.setType(MovementType.DESPESA); m.setAmount(new BigDecimal("7.00")); m.setDate(LocalDate.now());

            when(clientRepository.existsById(1L)).thenReturn(true);
            when(movementRepository.findByClientIdAndType(1L, MovementType.DESPESA))
                    .thenReturn(Collections.singletonList(m));

            assertEquals(1, service.listByClient(1L, null, null, MovementType.DESPESA).size());
        }

        @Test
        @DisplayName("deve listar por cliente + tipo + período")
        void list_typeAndPeriod() {
            LocalDate start = LocalDate.of(2025, 8, 1);
            LocalDate end   = LocalDate.of(2025, 8, 31);
            Movement m = new Movement(); m.setId(5L); m.setType(MovementType.RECEITA); m.setAmount(new BigDecimal("30.00")); m.setDate(LocalDate.of(2025,8,15));

            when(clientRepository.existsById(1L)).thenReturn(true);
            when(movementRepository.findByClientIdAndTypeAndDateBetween(1L, MovementType.RECEITA, start, end))
                    .thenReturn(Collections.singletonList(m));

            assertEquals(1, service.listByClient(1L, start, end, MovementType.RECEITA).size());
        }

        @Test
        @DisplayName("deve lançar 404 quando cliente não existe ao listar")
        void list_clientNotFound() {
            when(clientRepository.existsById(999L)).thenReturn(false);
            assertThrows(EntityNotFoundException.class,
                    () -> service.listByClient(999L, null, null, null));
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTests {

        @Test
        @DisplayName("deve retornar DTO ao buscar por id")
        void get_ok() {
            Movement m = new Movement();
            m.setId(123L);
            m.setType(MovementType.RECEITA);
            m.setAmount(new BigDecimal("77.00"));
            m.setDate(LocalDate.of(2025, 8, 10));

            when(movementRepository.findById(123L)).thenReturn(Optional.of(m));

            MovementResponseDTO dto = service.getById(123L);
            assertNotNull(dto);
            assertEquals(123L, dto.getId());
            assertEquals("RECEITA", dto.getType());
            assertEquals(new BigDecimal("77.00"), dto.getAmount());
        }

        @Test
        @DisplayName("deve lançar 404 quando movimentação não existe")
        void get_notFound() {
            when(movementRepository.findById(555L)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> service.getById(555L));
        }
    }
}
