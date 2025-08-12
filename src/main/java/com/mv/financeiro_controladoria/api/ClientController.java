package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.dto.*;
import com.mv.financeiro_controladoria.application.mapper.ClientMapper;
import com.mv.financeiro_controladoria.application.service.ClientService;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;
    private final ClientRepository clientRepository;

    public ClientController(ClientService service, ClientRepository clientRepository) {
        this.service = service;
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@RequestBody ClientCreateDTO dto) {
        Client c = service.create(dto);
        return ResponseEntity.ok(ClientMapper.toResponse(c));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> list() {
        List<ClientResponseDTO> list = clientRepository.findAll().stream()
                .map(ClientMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> get(@PathVariable Long id) {
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return ResponseEntity.ok(ClientMapper.toResponse(c));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id,
                                                    @RequestBody ClientUpdateDTO dto) {
        Client c = service.update(id, dto);
        return ResponseEntity.ok(ClientMapper.toResponse(c));
    }

    @GetMapping("/{id}/address")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long id) {
        AddressDTO addr = service.getAddress(id);
        return ResponseEntity.ok(addr);
    }

    @PutMapping("/{id}/address")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long id,
                                                    @RequestBody AddressDTO dto) {
        AddressDTO updated = service.updateAddress(id, dto);
        return ResponseEntity.ok(updated);
    }
}
