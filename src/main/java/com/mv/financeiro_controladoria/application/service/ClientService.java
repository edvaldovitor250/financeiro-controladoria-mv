package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.ClientCreateDTO;
import com.mv.financeiro_controladoria.application.mapper.ClientMapper;
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
}
