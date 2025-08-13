//package com.mv.financeiro_controladoria.application.usecase;
//
//import com.mv.financeiro_controladoria.application.dto.common.AddressDTO;
//import com.mv.financeiro_controladoria.application.dto.client.ClientCreateDTO;
//import com.mv.financeiro_controladoria.application.dto.client.ClientResponseDTO;
//import com.mv.financeiro_controladoria.application.dto.client.ClientUpdateDTO;
//import com.mv.financeiro_controladoria.application.mapper.ClientMapper;
//import com.mv.financeiro_controladoria.domain.entity.Address;
//import com.mv.financeiro_controladoria.domain.entity.Client;
//import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import javax.persistence.EntityNotFoundException;
//import java.util.Collections;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("ClientService – regras de criação, listagem, consulta e atualização")
//class ClientServiceTest {
//
//    @Mock
//    private ClientRepository clientRepository;
//
//    @Mock
//    private MovementService movementService;
//
//    @InjectMocks
//    private ClientService service;
//
//    private MockedStatic<ClientMapper> clientMapperStatic;
//
//    @BeforeEach
//    void setUp() {
//        if (clientMapperStatic != null) {
//            clientMapperStatic.close();
//        }
//        clientMapperStatic = Mockito.mockStatic(ClientMapper.class);
//    }
//
//
//    @Test
//    @DisplayName("create – Deve criar PF com movimentação inicial e CPF único")
//    void create_pf_sucesso() {
//        ClientCreateDTO dto = new ClientCreateDTO();
//        dto.personType = "PF";
//        dto.initialMovement = new Object(); // substitua pelo tipo real do seu projeto
//        dto.individual = new ClientCreateDTO.Individual();
//        dto.individual.cpf = "12345678901";
//
//        when(clientRepository.countByCpf("12345678901")).thenReturn(0L);
//
//        Client entity = mock(Client.class);
//        when(clientRepository.save(any(Client.class))).thenReturn(entity);
//
//        ClientResponseDTO expected = new ClientResponseDTO();
//        clientMapperStatic.when(() -> ClientMapper.toEntity(any(ClientCreateDTO.class))).thenReturn(entity);
//        clientMapperStatic.when(() -> ClientMapper.toResponse(entity)).thenReturn(expected);
//
//        ClientResponseDTO out = service.create(dto);
//
//        assertSame(expected, out);
//        verify(clientRepository).countByCpf("12345678901");
//        verify(clientRepository).save(any(Client.class));
//        verify(movementService).createForClient(entity, dto.initialMovement);
//    }
//
//    @Test
//    @DisplayName("create – Deve lançar erro ao criar PF com CPF duplicado")
//    void create_pf_cpfDuplicado() {
//        ClientCreateDTO dto = new ClientCreateDTO();
//        dto.personType = "PF";
//        dto.initialMovement = new Object();
//        dto.individual = new ClientCreateDTO.Individual();
//        dto.individual.cpf = "12345678901";
//
//        when(clientRepository.countByCpf("12345678901")).thenReturn(1L);
//
//        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.create(dto));
//        assertTrue(ex.getMessage().toLowerCase().contains("cpf"));
//        verify(clientRepository).countByCpf("12345678901");
//        verifyNoMoreInteractions(clientRepository);
//        verifyNoInteractions(movementService);
//    }
//
//    @Test
//    @DisplayName("create – Deve criar PJ com CNPJ único")
//    void create_pj_sucesso() {
//        ClientCreateDTO dto = new ClientCreateDTO();
//        dto.personType = "PJ";
//        dto.initialMovement = new Object();
//        dto.corporate = new ClientCreateDTO.Corporate();
//        dto.corporate.cnpj = "11222333000181";
//
//        when(clientRepository.countByCnpj("11222333000181")).thenReturn(0L);
//
//        Client entity = mock(Client.class);
//        when(clientRepository.save(any(Client.class))).thenReturn(entity);
//
//        ClientResponseDTO expected = new ClientResponseDTO();
//        clientMapperStatic.when(() -> ClientMapper.toEntity(any(ClientCreateDTO.class))).thenReturn(entity);
//        clientMapperStatic.when(() -> ClientMapper.toResponse(entity)).thenReturn(expected);
//
//        ClientResponseDTO out = service.create(dto);
//
//        assertSame(expected, out);
//        verify(clientRepository).countByCnpj("11222333000181");
//        verify(clientRepository).save(any(Client.class));
//        verify(movementService).createForClient(entity, dto.initialMovement);
//    }
//
//    @Test
//    @DisplayName("create – Deve lançar erro ao criar PJ com CNPJ duplicado")
//    void create_pj_cnpjDuplicado() {
//        ClientCreateDTO dto = new ClientCreateDTO();
//        dto.personType = "PJ";
//        dto.initialMovement = new Object();
//        dto.corporate = new ClientCreateDTO.Corporate();
//        dto.corporate.cnpj = "11222333000181";
//
//        when(clientRepository.countByCnpj("11222333000181")).thenReturn(1L);
//
//        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.create(dto));
//        assertTrue(ex.getMessage().toLowerCase().contains("cnpj"));
//        verify(clientRepository).countByCnpj("11222333000181");
//        verifyNoMoreInteractions(clientRepository);
//        verifyNoInteractions(movementService);
//    }
//
//    @Test
//    @DisplayName("create – Deve falhar quando initialMovement é nulo")
//    void create_semMovimentacaoInicial() {
//        ClientCreateDTO dto = new ClientCreateDTO();
//        dto.personType = "PF";
//        dto.initialMovement = null;
//        dto.individual = new ClientCreateDTO.Individual();
//        dto.individual.cpf = "12345678901";
//
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(dto));
//        assertTrue(ex.getMessage().toLowerCase().contains("obrigatória"));
//        verifyNoInteractions(clientRepository, movementService);
//    }
//
//    @Test
//    @DisplayName("create – Deve falhar com tipo de pessoa inválido")
//    void create_tipoPessoaInvalido() {
//        ClientCreateDTO dto = new ClientCreateDTO();
//        dto.personType = "XX";
//        dto.initialMovement = new Object();
//
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(dto));
//        assertTrue(ex.getMessage().toLowerCase().contains("inválido"));
//        verifyNoInteractions(clientRepository, movementService);
//    }
//
//    // ---------- LIST ----------
//
//    @Test
//    @DisplayName("list – Deve retornar página mapeada")
//    void list_sucesso() {
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Client c = mock(Client.class);
//        Page<Client> page = new PageImpl<>(Collections.singletonList(c), pageable, 1);
//
//        when(clientRepository.findAll(any(Pageable.class))).thenReturn(page);
//
//        ClientResponseDTO mapped = new ClientResponseDTO();
//        clientMapperStatic.when(() -> ClientMapper.toResponse(c)).thenReturn(mapped);
//
//        Page<ClientResponseDTO> result = service.list(pageable);
//
//        assertEquals(1, result.getTotalElements());
//        assertSame(mapped, result.getContent().get(0));
//        verify(clientRepository).findAll(any(Pageable.class));
//    }
//
//
//    @Test
//    @DisplayName("getById – Deve retornar DTO quando existir")
//    void getById_sucesso() {
//        Client c = mock(Client.class);
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(c));
//
//        ClientResponseDTO mapped = new ClientResponseDTO();
//        clientMapperStatic.when(() -> ClientMapper.toResponse(c)).thenReturn(mapped);
//
//        ClientResponseDTO out = service.getById(1L);
//
//        assertSame(mapped, out);
//        verify(clientRepository).findById(1L);
//    }
//
//    @Test
//    @DisplayName("getById – Deve lançar EntityNotFoundException quando não existir")
//    void getById_naoEncontrado() {
//        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
//        assertThrows(EntityNotFoundException.class, () -> service.getById(99L));
//    }
//
//
//    @Test
//    @DisplayName("update – Deve atualizar nome, telefone e endereço")
//    void update_sucesso() {
//        Client c = mock(Client.class);
//
//        @SuppressWarnings("unchecked")
//        Enum<?> personType = Enum.valueOf((Class) DummyPersonType.class, "PF");
//        when(c.getPersonType()).thenReturn(personType);
//
//        Address existing = new Address();
//        when(c.getAddress()).thenReturn(existing);
//
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(c));
//        when(clientRepository.save(c)).thenReturn(c);
//
//        ClientUpdateDTO dto = new ClientUpdateDTO();
//        dto.name = "Novo Nome";
//        dto.phone = "9999-9999";
//        dto.address = new AddressDTO();
//        dto.address.street = "Rua A";
//        dto.address.city = "Cidade B";
//        dto.address.state = "ST";
//        dto.address.zipCode = "00000-000";
//        dto.address.complement = "Casa";
//
//        ClientResponseDTO mapped = new ClientResponseDTO();
//        clientMapperStatic.when(() -> ClientMapper.toResponse(c)).thenReturn(mapped);
//
//        ClientResponseDTO out = service.update(1L, dto);
//
//        assertSame(mapped, out);
//        verify(c).setName("Novo Nome");
//        verify(c).setPhone("9999-9999");
//        assertEquals("Rua A", existing.getStreet());
//        assertEquals("Cidade B", existing.getCity());
//        assertEquals("ST", existing.getState());
//        assertEquals("00000-000", existing.getZipCode());
//        assertEquals("Casa", existing.getComplement());
//        verify(clientRepository).save(c);
//    }
//
//
//    @Test
//    @DisplayName("getAddress – Deve retornar AddressDTO a partir da entidade")
//    void getAddress_sucesso() {
//        Client c = mock(Client.class);
//        Address a = new Address();
//        when(c.getAddress()).thenReturn(a);
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(c));
//
//        try (MockedStatic<AddressDTO> addressStatic = Mockito.mockStatic(AddressDTO.class)) {
//            AddressDTO expected = new AddressDTO();
//            addressStatic.when(() -> AddressDTO.from(a)).thenReturn(expected);
//
//            AddressDTO out = service.getAddress(1L);
//            assertSame(expected, out);
//        }
//    }
//
//    @Test
//    @DisplayName("updateAddress – Deve salvar alterações de endereço e retornar DTO")
//    void updateAddress_sucesso() {
//        Client c = mock(Client.class);
//        Address current = new Address();
//        when(c.getAddress()).thenReturn(current);
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(c));
//
//        AddressDTO dto = new AddressDTO();
//        dto.street = "Rua X";
//        dto.city = "Cidade Y";
//        dto.state = "ST";
//        dto.zipCode = "11111-111";
//        dto.complement = "Sala 2";
//
//        try (MockedStatic<AddressDTO> addressStatic = Mockito.mockStatic(AddressDTO.class)) {
//            AddressDTO expected = new AddressDTO();
//            addressStatic.when(() -> AddressDTO.from(any(Address.class))).thenReturn(expected);
//
//            AddressDTO out = service.updateAddress(1L, dto);
//            assertSame(expected, out);
//        }
//
//        assertEquals("Rua X", current.getStreet());
//        assertEquals("Cidade Y", current.getCity());
//        assertEquals("ST", current.getState());
//        assertEquals("11111-111", current.getZipCode());
//        assertEquals("Sala 2", current.getComplement());
//        verify(clientRepository).save(c);
//    }
//
//    private enum DummyPersonType { PF, PJ }
//}
