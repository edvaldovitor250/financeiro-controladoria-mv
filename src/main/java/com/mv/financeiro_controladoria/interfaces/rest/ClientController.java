package com.mv.financeiro_controladoria.interfaces.rest;

import com.mv.financeiro_controladoria.application.dto.client.ClientCreateDTO;
import com.mv.financeiro_controladoria.application.dto.client.ClientResponseDTO;
import com.mv.financeiro_controladoria.application.dto.client.ClientUpdateDTO;
import com.mv.financeiro_controladoria.application.dto.common.AddressDTO;
import com.mv.financeiro_controladoria.application.mapper.ClientMapper;
import com.mv.financeiro_controladoria.application.usecase.ClientService;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "CRUD de clientes e endereço")
public class ClientController {

    private final ClientService service;
    private final ClientRepository clientRepository;

    @Operation(summary = "Cadastrar cliente (PF/PJ) com movimentação inicial obrigatória")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente criado"),
            @ApiResponse(responseCode = "400", description = "Validação ou regra de negócio")
    })
    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@RequestBody ClientCreateDTO dto) {
        Client c = service.create(dto);
        return ResponseEntity.ok(ClientMapper.toResponse(c));
    }

    @Operation(summary = "Listar clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> list() {
        List<ClientResponseDTO> list = clientRepository.findAll().stream()
                .map(ClientMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> get(@PathVariable Long id) {
        Client c = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return ResponseEntity.ok(ClientMapper.toResponse(c));
    }

    @Operation(summary = "Atualizar dados do cliente (mantém histórico sensível)")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id,
                                                    @RequestBody ClientUpdateDTO dto) {
        Client c = service.update(id, dto);
        return ResponseEntity.ok(ClientMapper.toResponse(c));
    }

    @Operation(summary = "Obter endereço do cliente")
    @ApiResponse(responseCode = "200", description = "Endereço")
    @GetMapping("/{id}/address")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long id) {
        AddressDTO addr = service.getAddress(id);
        return ResponseEntity.ok(addr);
    }

    @Operation(summary = "Atualizar endereço do cliente")
    @ApiResponse(responseCode = "200", description = "Endereço atualizado")
    @PutMapping("/{id}/address")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long id,
                                                    @RequestBody AddressDTO dto) {
        AddressDTO updated = service.updateAddress(id, dto);
        return ResponseEntity.ok(updated);
    }
}
