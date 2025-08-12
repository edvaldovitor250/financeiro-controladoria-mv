package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.AddressDTO;
import com.mv.financeiro_controladoria.application.dto.ClientCreateDTO;
import com.mv.financeiro_controladoria.application.dto.ClientUpdateDTO;
import com.mv.financeiro_controladoria.application.mapper.ClientMapper;
import com.mv.financeiro_controladoria.domain.model.Address;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final MovementService movementService;

    @Transactional
    public Client create(ClientCreateDTO dto) {
        if (dto.initialMovement == null) {
            throw new IllegalArgumentException("Movimentação inicial é obrigatória.");
        }
        if (dto.personType == null) {
            throw new IllegalArgumentException("Tipo de pessoa (PF/PJ) é obrigatório.");
        }

        if ("PF".equalsIgnoreCase(dto.personType)) {
            String cpf = (dto.individual != null ? dto.individual.cpf : null);
            if (cpf == null || cpf.trim().isEmpty()) {
                throw new IllegalArgumentException("CPF é obrigatório para PF.");
            }
            if (clientRepository.countByCpf(cpf.trim()) > 0) {
                throw new IllegalStateException("CPF já cadastrado.");
            }
        } else if ("PJ".equalsIgnoreCase(dto.personType)) {
            String cnpj = (dto.corporate != null ? dto.corporate.cnpj : null);
            if (cnpj == null || cnpj.trim().isEmpty()) {
                throw new IllegalArgumentException("CNPJ é obrigatório para PJ.");
            }
            if (clientRepository.countByCnpj(cnpj.trim()) > 0) {
                throw new IllegalStateException("CNPJ já cadastrado.");
            }
        } else {
            throw new IllegalArgumentException("Tipo de pessoa inválido: " + dto.personType);
        }

        Client entity = ClientMapper.toEntity(dto);
        Client saved = clientRepository.save(entity);
        movementService.createForClient(saved, dto.initialMovement);
        return saved;
    }


    @Transactional
    public Client update(Long id, ClientUpdateDTO dto) {
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        if (dto.personType != null && !dto.personType.equalsIgnoreCase(c.getPersonType().name())) {
            throw new IllegalStateException("Tipo de pessoa não pode ser alterado.");
        }
        if (c instanceof com.mv.financeiro_controladoria.domain.model.IndividualClient && dto.individual != null && dto.individual.cpf != null) {
            String atual = ((com.mv.financeiro_controladoria.domain.model.IndividualClient)c).getCpf();
            if (atual != null && !atual.equals(dto.individual.cpf)) {
                throw new IllegalStateException("CPF não pode ser alterado.");
            }
        }
        if (c instanceof com.mv.financeiro_controladoria.domain.model.CorporateClient && dto.corporate != null && dto.corporate.cnpj != null) {
            String atual = ((com.mv.financeiro_controladoria.domain.model.CorporateClient)c).getCnpj();
            if (atual != null && !atual.equals(dto.corporate.cnpj)) {
                throw new IllegalStateException("CNPJ não pode ser alterado.");
            }
        }

        if (dto.name != null)  c.setName(dto.name);
        if (dto.phone != null) c.setPhone(dto.phone);
        if (dto.address != null) {
            Address a = c.getAddress() != null ? c.getAddress() : new Address();
            a.setStreet(dto.address.street);
            a.setCity(dto.address.city);
            a.setState(dto.address.state);
            a.setZipCode(dto.address.zipCode);
            a.setComplement(dto.address.complement);
            c.setAddress(a);
        }
        return clientRepository.save(c);
    }

    @Transactional(readOnly = true)
    public AddressDTO getAddress(Long clientId) {
        Client c = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return AddressDTO.from(c.getAddress());
    }

    @Transactional
    public AddressDTO updateAddress(Long clientId, AddressDTO dto) {
        Client c = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Address a = c.getAddress() != null ? c.getAddress() : new Address();
        a.setStreet(dto.street);
        a.setCity(dto.city);
        a.setState(dto.state);
        a.setZipCode(dto.zipCode);
        a.setComplement(dto.complement);
        c.setAddress(a);
        clientRepository.save(c);
        return AddressDTO.from(a);
    }
}
