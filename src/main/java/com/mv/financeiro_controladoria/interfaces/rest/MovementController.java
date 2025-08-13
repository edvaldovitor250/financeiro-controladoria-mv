package com.mv.financeiro_controladoria.interfaces.rest;

import com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO;
import com.mv.financeiro_controladoria.application.dto.movement.MovementResponseDTO;
import com.mv.financeiro_controladoria.application.usecase.MovementService;
import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
@Tag(name = "Movements", description = "Cadastro e consulta de movimentações")
@Validated
public class MovementController {

    private final MovementService movementService;

    @Operation(summary = "Criar movimentação para um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimentação criada"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos / Regra de negócio"),
            @ApiResponse(responseCode = "404", description = "Cliente/Conta não encontrada")
    })
    @PostMapping("/clients/{clientId}")
    public ResponseEntity<MovementResponseDTO> create(@PathVariable @Positive Long clientId,
                                                      @Valid @RequestBody MovementCreateDTO dto,
                                                      UriComponentsBuilder uri) {
        MovementResponseDTO created = movementService.createForClient(clientId, dto);
        URI location = uri.path("/api/movements/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Listar movimentações do cliente com filtros opcionais (período e tipo)")
    @ApiResponse(responseCode = "200", description = "Lista de movimentações")
    @GetMapping("/clients/{clientId}")
    public ResponseEntity<List<MovementResponseDTO>> list(
            @PathVariable @Positive Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate end,
            @RequestParam(required = false) MovementType type) {

        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("start não pode ser maior que end.");
        }
        return ResponseEntity.ok(movementService.listByClient(clientId, start, end, type));
    }

    @Operation(summary = "Buscar movimentação por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimentação encontrada"),
            @ApiResponse(responseCode = "404", description = "Movimentação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovementResponseDTO> get(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(movementService.getById(id));
    }
}
