package com.mv.financeiro_controladoria.interfaces.rest;

import com.mv.financeiro_controladoria.application.dto.client.ClientCreateDTO;
import com.mv.financeiro_controladoria.application.dto.client.ClientResponseDTO;
import com.mv.financeiro_controladoria.application.dto.client.ClientUpdateDTO;
import com.mv.financeiro_controladoria.application.dto.common.AddressDTO;
import com.mv.financeiro_controladoria.application.usecase.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "CRUD de clientes e endereço")
@Validated
public class ClientController {

    private final ClientService service;

    @Operation(summary = "Cadastrar cliente (PF/PJ) com movimentação inicial obrigatória")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado"),
            @ApiResponse(responseCode = "400", description = "Validação ou regra de negócio")
    })
    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody ClientCreateDTO dto,
                                                    UriComponentsBuilder uriBuilder) {
        ClientResponseDTO created = service.create(dto);
        URI location = uriBuilder.path("/api/clients/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Listar clientes (paginado)")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list((java.awt.print.Pageable) pageable));
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Atualizar dados do cliente (mantém histórico sensível)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Validação ou regra de negócio")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody ClientUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Obter endereço do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}/address")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAddress(id));
    }

    @Operation(summary = "Atualizar endereço do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço atualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Validação ou regra de negócio")
    })
    @PutMapping("/{id}/address")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long id,
                                                    @Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(service.updateAddress(id, dto));
    }
}
