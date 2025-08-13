//package com.mv.financeiro_controladoria.application.usecase;
//
//import com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO;
//import com.mv.financeiro_controladoria.application.dto.movement.MovementResponseDTO;
//import com.mv.financeiro_controladoria.domain.entity.Account;
//import com.mv.financeiro_controladoria.domain.entity.Client;
//import com.mv.financeiro_controladoria.domain.entity.Movement;
//import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
//import com.mv.financeiro_controladoria.infra.persistence.repository.AccountRepository;
//import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
//import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
//import lombok.var;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import javax.persistence.EntityNotFoundException;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("MovementService – criação, listagem e consulta de movimentações")
//class MovementServiceTest {
//
//    @Mock private MovementRepository movementRepository;
//    @Mock private AccountRepository accountRepository;
//    @Mock private ClientRepository clientRepository;
//
//    @InjectMocks private MovementService movementService;
//
//    private Client client;
//    private Account account;
//
//    private MockedStatic<MovementResponseDTO> movementResponseStatic;
//
//    @BeforeEach
//    void setUp() {
//        client = new Client();
//        client.setId(10L);
//        client.setName("Cliente X");
//
//        account = new Account();
//        account.setId(50L);
//        account.setClient(client);
//        account.setActive(true);
//
//        if (movementResponseStatic != null) {
//            movementResponseStatic.close();
//        }
//        movementResponseStatic = Mockito.mockStatic(MovementResponseDTO.class);
//    }
//
//
//    @Test
//    @DisplayName("createForClient(Long, DTO) – Deve criar movimentação sem conta vinculada")
//    void createForClient_semConta() {
//        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
//
//        MovementCreateDTO dto = new MovementCreateDTO();
//        dto.setType(MovementType.RECEITA);
//        dto.setAmount(new BigDecimal("123.456"));
//        dto.setDescription("Ajuste inicial");
//        dto.setDate(LocalDate.of(2025, 8, 1));
//        dto.setAccountId(null);
//
//        ArgumentCaptor<Movement> captor = ArgumentCaptor.forClass(Movement.class);
//        Movement persisted = new Movement();
//        when(movementRepository.save(any(Movement.class))).thenAnswer(inv -> {
//            Movement m = inv.getArgument(0);
//            persisted.setId(1L);
//            persisted.setClient(m.getClient());
//            persisted.setAccount(m.getAccount());
//            persisted.setAmount(m.getAmount());
//            persisted.setType(m.getType());
//            persisted.setDescription(m.getDescription());
//            persisted.setDate(m.getDate());
//            return persisted;
//        });
//
//        MovementResponseDTO expected = new MovementResponseDTO();
//        movementResponseStatic.when(() -> MovementResponseDTO.from(any(Movement.class))).thenReturn(expected);
//
//        MovementResponseDTO out = movementService.createForClient(10L, dto);
//
//        assertSame(expected, out);
//        verify(movementRepository).save(captor.capture());
//        Movement saved = captor.getValue();
//
//        assertEquals(client, saved.getClient());
//        assertNull(saved.getAccount());
//        assertEquals(new BigDecimal("123.46"), saved.getAmount());
//        assertEquals(MovementType.DESPESA, saved.getType());
//        assertEquals("Ajuste inicial", saved.getDescription());
//        assertEquals(LocalDate.of(2025, 8, 1), saved.getDate());
//    }
//
//    @Test
//    @DisplayName("createForClient(Long, DTO) – Deve associar conta válida do mesmo cliente")
//    void createForClient_comContaValida() {
//        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
//        when(accountRepository.findById(50L)).thenReturn(Optional.of(account));
//
//        MovementCreateDTO dto = new MovementCreateDTO();
//        dto.setType(MovementType.RECEITA);
//        dto.setAmount(new BigDecimal("50"));
//        dto.setDescription("Pagamento");
//        dto.setDate(LocalDate.of(2025, 8, 2));
//        dto.setAccountId(50L);
//
//        when(movementRepository.save(any(Movement.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        MovementResponseDTO expected = new MovementResponseDTO();
//        movementResponseStatic.when(() -> MovementResponseDTO.from(any(Movement.class))).thenReturn(expected);
//
//        MovementResponseDTO out = movementService.createForClient(10L, dto);
//
//        assertSame(expected, out);
//        verify(accountRepository).findById(50L);
//        verify(movementRepository).save(any(Movement.class));
//    }
//
//    @Test
//    @DisplayName("createForClient(Long, DTO) – Deve falhar se conta pertence a outro cliente")
//    void createForClient_contaDeOutroCliente() {
//        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
//
//        Client outro = new Client();
//        outro.setId(99L);
//        Account accOutro = new Account();
//        accOutro.setId(77L);
//        accOutro.setClient(outro);
//        accOutro.setActive(true);
//
//        when(accountRepository.findById(77L)).thenReturn(Optional.of(accOutro));
//
//        MovementCreateDTO dto = new MovementCreateDTO();
//        dto.setType(MovementType.RECEITA);
//        dto.setAmount(new BigDecimal("10"));
//        dto.setDescription("Teste");
//        dto.setDate(LocalDate.of(2025, 8, 3));
//        dto.setAccountId(77L);
//
//        IllegalStateException ex = assertThrows(IllegalStateException.class,
//                () -> movementService.createForClient(10L, dto));
//        assertTrue(ex.getMessage().toLowerCase().contains("não pertence"));
//        verifyNoInteractions(movementRepository);
//    }
//
//    @Test
//    @DisplayName("createForClient(Long, DTO) – Deve falhar se conta está inativa")
//    void createForClient_contaInativa() {
//        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
//        account.setActive(false);
//        when(accountRepository.findById(50L)).thenReturn(Optional.of(account));
//
//        MovementCreateDTO dto = new MovementCreateDTO();
//        dto.setType(MovementType.RECEITA);
//        dto.setAmount(new BigDecimal("20"));
//        dto.setDescription("Tarifa");
//        dto.setDate(LocalDate.of(2025, 8, 4));
//        dto.setAccountId(50L);
//
//        IllegalStateException ex = assertThrows(IllegalStateException.class,
//                () -> movementService.createForClient(10L, dto));
//        assertTrue(ex.getMessage().toLowerCase().contains("inativa"));
//        verifyNoInteractions(movementRepository);
//    }
//
//    @Test
//    @DisplayName("createForClient(Long, DTO) – Deve falhar se cliente não existe")
//    void createForClient_clienteInexistente() {
//        when(clientRepository.findById(10L)).thenReturn(Optional.empty());
//
//        MovementCreateDTO dto = new MovementCreateDTO();
//        dto.setType(MovementType.RECEITA);
//        dto.setAmount(new BigDecimal("30"));
//        dto.setDescription("Crédito");
//        dto.setDate(LocalDate.of(2025, 8, 5));
//
//        assertThrows(EntityNotFoundException.class, () -> movementService.createForClient(10L, dto));
//        verifyNoInteractions(movementRepository);
//    }
//
//    @Test
//    @DisplayName("createForClient(Client, DTO) – Atalho deve delegar para a sobrecarga por id")
//    void createForClient_overloadCliente() {
//        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
//        MovementCreateDTO dto = new MovementCreateDTO();
//        dto.setType(MovementType.RECEITA);
//        dto.setAmount(new BigDecimal("1"));
//        dto.setDescription("x");
//        dto.setDate(LocalDate.of(2025, 8, 6));
//
//        when(movementRepository.save(any(Movement.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        MovementResponseDTO expected = new MovementResponseDTO();
//        movementResponseStatic.when(() -> MovementResponseDTO.from(any(Movement.class))).thenReturn(expected);
//
//        MovementResponseDTO out = movementService.createForClient(client, dto);
//        assertSame(expected, out);
//        verify(clientRepository).findById(10L);
//    }
//
//
//    @Test
//    @DisplayName("listByClient – Filtro por intervalo e tipo")
//    void listByClient_intervaloETipo() {
//        when(clientRepository.existsById(10L)).thenReturn(true);
//
//        Movement m1 = new Movement(); m1.setId(1L);
//        Movement m2 = new Movement(); m2.setId(2L);
//
//        when(movementRepository.findByClientIdAndTypeAndDateBetween(
//                eq(10L), eq(MovementType.RECEITA),
//                eq(LocalDate.of(2025, 8, 1)), eq(LocalDate.of(2025, 8, 31))
//        )).thenReturn(Arrays.asList(m1, m2));
//
//        MovementResponseDTO dto1 = new MovementResponseDTO();
//        MovementResponseDTO dto2 = new MovementResponseDTO();
//        movementResponseStatic.when(() -> MovementResponseDTO.from(m1)).thenReturn(dto1);
//        movementResponseStatic.when(() -> MovementResponseDTO.from(m2)).thenReturn(dto2);
//
//        var list = movementService.listByClient(
//                10L, LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 31), MovementType.DESPESA);
//
//        assertEquals(2, list.size());
//        assertSame(dto1, list.get(0));
//        assertSame(dto2, list.get(1));
//    }
//
//    @Test
//    @DisplayName("listByClient – Apenas intervalo de datas")
//    void listByClient_apenasIntervalo() {
//        when(clientRepository.existsById(10L)).thenReturn(true);
//
//        Movement m = new Movement(); m.setId(3L);
//        when(movementRepository.findByClientIdAndDateBetween(
//                eq(10L), eq(LocalDate.of(2025, 7, 1)), eq(LocalDate.of(2025, 7, 31))
//        )).thenReturn(Collections.singletonList(m));
//
//        MovementResponseDTO dto = new MovementResponseDTO();
//        movementResponseStatic.when(() -> MovementResponseDTO.from(m)).thenReturn(dto);
//
//        var list = movementService.listByClient(
//                10L, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31), null);
//
//        assertEquals(1, list.size());
//        assertSame(dto, list.get(0));
//    }
//
//    @Test
//    @DisplayName("listByClient – Apenas tipo")
//    void listByClient_apenasTipo() {
//        when(clientRepository.existsById(10L)).thenReturn(true);
//
//        Movement m = new Movement(); m.setId(4L);
//        when(movementRepository.findByClientIdAndType(10L, MovementType.RECEITA))
//                .thenReturn(Collections.singletonList(m));
//
//        MovementResponseDTO dto = new MovementResponseDTO();
//        movementResponseStatic.when(() -> MovementResponseDTO.from(m)).thenReturn(dto);
//
//        var list = movementService.listByClient(10L, null, null, MovementType.RECEITA);
//
//        assertEquals(1, list.size());
//        assertSame(dto, list.get(0));
//    }
//
//    @Test
//    @DisplayName("listByClient – Sem filtros")
//    void listByClient_semFiltros() {
//        when(clientRepository.existsById(10L)).thenReturn(true);
//
//        Movement m = new Movement(); m.setId(5L);
//        when(movementRepository.findByClientId(10L)).thenReturn(Collections.singletonList(m));
//
//        MovementResponseDTO dto = new MovementResponseDTO();
//        movementResponseStatic.when(() -> MovementResponseDTO.from(m)).thenReturn(dto);
//
//        var list = movementService.listByClient(10L, null, null, null);
//
//        assertEquals(1, list.size());
//        assertSame(dto, list.get(0));
//    }
//
//    @Test
//    @DisplayName("listByClient – Deve lançar EntityNotFoundException se cliente não existir")
//    void listByClient_clienteInexistente() {
//        when(clientRepository.existsById(10L)).thenReturn(false);
//        assertThrows(EntityNotFoundException.class,
//                () -> movementService.listByClient(10L, null, null, null));
//        verifyNoInteractions(movementRepository);
//    }
//
//
//    @Test
//    @DisplayName("getById – Deve retornar DTO quando existir")
//    void getById_sucesso() {
//        Movement m = new Movement(); m.setId(100L);
//        when(movementRepository.findById(100L)).thenReturn(Optional.of(m));
//
//        MovementResponseDTO dto = new MovementResponseDTO();
//        movementResponseStatic.when(() -> MovementResponseDTO.from(m)).thenReturn(dto);
//
//        MovementResponseDTO out = movementService.getById(100L);
//        assertSame(dto, out);
//    }
//
//    @Test
//    @DisplayName("getById – Deve lançar EntityNotFoundException quando não existir")
//    void getById_naoEncontrado() {
//        when(movementRepository.findById(404L)).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> movementService.getById(404L));
//    }
//}
