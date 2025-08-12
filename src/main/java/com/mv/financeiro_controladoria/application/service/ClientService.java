package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.AddressDTO;
import com.mv.financeiro_controladoria.application.dto.ClientCreateDTO;
import com.mv.financeiro_controladoria.application.dto.ClientUpdateDTO;
import com.mv.financeiro_controladoria.application.mapper.ClientMapper;
import com.mv.financeiro_controladoria.domain.model.Address;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final MovementService movementService;

    public ClientService(ClientRepository clientRepository, MovementService movementService) {
        this.clientRepository = clientRepository;
        this.movementService = movementService;
    }

    @Transactional
    public Client create(ClientCreateDTO dto) {
        if (dto.initialMovement == null) {
            throw new IllegalArgumentException("Movimentação inicial é obrigatória.");
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
        if (dto.name != null) c.setName(dto.name);
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
