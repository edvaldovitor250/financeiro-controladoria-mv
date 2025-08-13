package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.application.dto.client.*;
import com.mv.financeiro_controladoria.application.dto.common.AddressDTO;
import com.mv.financeiro_controladoria.domain.entity.*;
import com.mv.financeiro_controladoria.domain.entity.enums.PersonType;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock MovementService movementService;

    @InjectMocks ClientService service;

    @Test
    void create_pf_ok() {
        ClientCreateDTO dto = new ClientCreateDTO();
        dto.personType = "PF";
        dto.name = "Ana";
        dto.phone = "1199999-1111";
        dto.individual = new IndividualDataDTO();
        dto.individual.cpf = "12345678901";
        dto.initialMovement = TestHelper.sampleInitialMovement();

        when(clientRepository.countByCpf("12345678901")).thenReturn(0L);
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> {
            Client c = i.getArgument(0);
            c.setId(1L);
            c.setPersonType(PersonType.PF);
            return c;
        });

        Client c = service.create(dto);

        assertNotNull(c.getId());
        verify(movementService).createForClient(eq(c), eq(dto.initialMovement));
    }

    @Test
    void create_pf_duplicado_disparaErro() {
        ClientCreateDTO dto = new ClientCreateDTO();
        dto.personType = "PF";
        dto.individual = new IndividualDataDTO();
        dto.individual.cpf = "123";
        dto.initialMovement = TestHelper.sampleInitialMovement();

        when(clientRepository.countByCpf("123")).thenReturn(1L);
        assertThrows(IllegalStateException.class, () -> service.create(dto));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void create_pj_semCnpj_disparaErro() {
        ClientCreateDTO dto = new ClientCreateDTO();
        dto.personType = "PJ";
        dto.initialMovement = TestHelper.sampleInitialMovement();

        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test
    void update_bloqueiaTrocaPersonType() {
        Client c = new IndividualClient();
        c.setId(5L);
        c.setPersonType(PersonType.PF);
        when(clientRepository.findById(5L)).thenReturn(Optional.of(c));

        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.personType = "PJ";

        assertThrows(IllegalStateException.class, () -> service.update(5L, dto));
    }

    @Test
    void update_bloqueiaTrocaCpf() {
        IndividualClient c = new IndividualClient();
        c.setId(7L);
        c.setPersonType(PersonType.PF);
        c.setCpf("111");
        when(clientRepository.findById(7L)).thenReturn(Optional.of(c));

        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.individual = new ClientUpdateDTO.Individual();
        dto.individual.cpf = "222";

        assertThrows(IllegalStateException.class, () -> service.update(7L, dto));
    }

    @Test
    void getAddress_ok() {
        Client c = new Client();
        Address a = new Address();
        a.setStreet("Rua A"); a.setCity("SP"); a.setState("SP"); a.setZipCode("01000-000");
        c.setAddress(a);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(c));

        AddressDTO dto = service.getAddress(1L);
        assertEquals("Rua A", dto.street);
        assertEquals("SP", dto.city);
        assertEquals("SP", dto.state);
        assertEquals("01000-000", dto.zipCode);
    }

    static class TestHelper {
        static com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO sampleInitialMovement() {
            com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO m =
                    new com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO();
            m.type = "RECEITA";
            m.amount = new java.math.BigDecimal("100.00");
            m.description = "saldo inicial";
            m.date = java.time.LocalDate.of(2025, 1, 1);
            return m;
        }
    }
}
