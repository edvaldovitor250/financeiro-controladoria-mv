package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.common.AddressDTO;
import com.mv.financeiro_controladoria.application.dto.client.ClientCreateDTO;
import com.mv.financeiro_controladoria.application.dto.client.ClientUpdateDTO;
import com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO;
import com.mv.financeiro_controladoria.domain.entity.Address;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.application.usecase.ClientService;
import com.mv.financeiro_controladoria.application.usecase.MovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private MovementService movementService;

    @InjectMocks
    private ClientService service;

    static class TestClient extends Client {}

    private TestClient existing;

    @BeforeEach
    void setUp() {
        existing = new TestClient();
        existing.setName("Fulano");
        existing.setPhone("1199999-0000");
        Address addr = new Address();
        addr.setStreet("Rua A");
        addr.setCity("SP");
        addr.setState("SP");
        addr.setZipCode("01000-000");
        existing.setAddress(addr);
    }

    // -------------------- create --------------------

    @Test
    void create_deve_salvar_cliente_e_criar_movimentacao_inicial() {
        ClientCreateDTO dto = new ClientCreateDTO();
        dto.personType = "PF";
        dto.name = "Maria";
        dto.phone = "1198888-7777";

        AddressDTO a = new AddressDTO();
        a.street = "Rua B"; a.city = "RJ"; a.state = "RJ"; a.zipCode = "20000-000";
        dto.address = a;

        MovementCreateDTO m = new MovementCreateDTO();
        m.type = "RECEITA";
        m.amount = new BigDecimal("100.00");
        m.description = "Saldo inicial";
        m.date = LocalDate.now();
        dto.initialMovement = m;

        when(clientRepository.save(any(Client.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Client saved = service.create(dto);

        assertNotNull(saved, "Cliente retornado não deveria ser nulo");
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(movementService, times(1)).createForClient(eq(saved), eq(dto.initialMovement));
        verifyNoMoreInteractions(movementService);
    }

    @Test
    void create_deve_lancar_quando_nao_tem_movimentacao_inicial() {
        ClientCreateDTO dto = new ClientCreateDTO();
        dto.personType = "PF";
        dto.name = "João";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(dto));
        assertTrue(ex.getMessage().contains("Movimentação inicial"));

        verify(clientRepository, never()).save(any());
        verify(movementService, never()).createForClient(any(), any());
    }

    // -------------------- update --------------------

    @Test
    void update_deve_atualizar_nome_telefone_e_endereco() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.name = "Fulano Atualizado";
        dto.phone = "1191111-2222";
        dto.address = new AddressDTO();
        dto.address.street = "Rua Nova";
        dto.address.city   = "Campinas";
        dto.address.state  = "SP";
        dto.address.zipCode = "13000-000";
        dto.address.complement = "Casa 2";

        Client updated = service.update(1L, dto);

        assertEquals("Fulano Atualizado", updated.getName());
        assertEquals("1191111-2222", updated.getPhone());
        assertNotNull(updated.getAddress());
        assertEquals("Rua Nova", updated.getAddress().getStreet());
        assertEquals("Campinas", updated.getAddress().getCity());
        assertEquals("SP", updated.getAddress().getState());
        assertEquals("13000-000", updated.getAddress().getZipCode());
        assertEquals("Casa 2", updated.getAddress().getComplement());
    }

    @Test
    void update_deve_criar_endereco_quando_cliente_nao_tinha() {
        TestClient semEndereco = new TestClient();
        semEndereco.setName("Sem Endereço");
        semEndereco.setPhone("110000-0000");
        when(clientRepository.findById(2L)).thenReturn(Optional.of(semEndereco));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.address = new AddressDTO();
        dto.address.street = "Rua 1";
        dto.address.city   = "BH";
        dto.address.state  = "MG";
        dto.address.zipCode = "30000-000";

        Client updated = service.update(2L, dto);

        assertNotNull(updated.getAddress());
        assertEquals("Rua 1", updated.getAddress().getStreet());
        assertEquals("BH", updated.getAddress().getCity());
        assertEquals("MG", updated.getAddress().getState());
        assertEquals("30000-000", updated.getAddress().getZipCode());
    }

    @Test
    void update_deve_lancar_quando_cliente_nao_existe() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        ClientUpdateDTO dto = new ClientUpdateDTO();
        assertThrows(IllegalArgumentException.class, () -> service.update(99L, dto));
        verify(clientRepository, never()).save(any());
    }


    @Test
    void getAddress_deve_retornar_addressDTO_do_cliente() {
        when(clientRepository.findById(3L)).thenReturn(Optional.of(existing));

        AddressDTO dto = service.getAddress(3L);

        assertNotNull(dto);
        assertEquals("Rua A", dto.street);
        assertEquals("SP", dto.city);
        assertEquals("SP", dto.state);
        assertEquals("01000-000", dto.zipCode);
    }

    @Test
    void getAddress_deve_lancar_quando_cliente_nao_existe() {
        when(clientRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getAddress(3L));
    }


    @Test
    void updateAddress_deve_atualizar_endereco_e_salvar() {
        when(clientRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        AddressDTO dto = new AddressDTO();
        dto.street = "Alameda XPTO";
        dto.city = "Curitiba";
        dto.state = "PR";
        dto.zipCode = "80000-000";
        dto.complement = "Bloco B";

        AddressDTO resp = service.updateAddress(4L, dto);

        assertEquals("Alameda XPTO", resp.street);
        assertEquals("Curitiba", resp.city);
        assertEquals("PR", resp.state);
        assertEquals("80000-000", resp.zipCode);
        assertEquals("Bloco B", resp.complement);

        assertEquals("Curitiba", existing.getAddress().getCity());
    }

    @Test
    void updateAddress_deve_criar_endereco_quando_nulo_e_salvar() {
        TestClient c = new TestClient();
        c.setName("Novo");
        c.setPhone("113333-4444");
        c.setAddress(null);

        when(clientRepository.findById(5L)).thenReturn(Optional.of(c));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        AddressDTO dto = new AddressDTO();
        dto.street = "Rua Zero";
        dto.city = "Porto Alegre";
        dto.state = "RS";
        dto.zipCode = "90000-000";

        AddressDTO resp = service.updateAddress(5L, dto);

        assertNotNull(resp);
        assertNotNull(c.getAddress());
        assertEquals("Porto Alegre", c.getAddress().getCity());
    }

    @Test
    void updateAddress_deve_lancar_quando_cliente_nao_existe() {
        when(clientRepository.findById(6L)).thenReturn(Optional.empty());
        AddressDTO dto = new AddressDTO();
        assertThrows(IllegalArgumentException.class, () -> service.updateAddress(6L, dto));
        verify(clientRepository, never()).save(any());
    }
}
