package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.dto.MovementCreateDTO;
import com.mv.financeiro_controladoria.application.dto.MovementResponseDTO;
import com.mv.financeiro_controladoria.application.service.MovementService;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.model.Movement;
import com.mv.financeiro_controladoria.domain.model.enums.MovementType;
import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
import com.mv.financeiro_controladoria.domain.repository.MovementRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
@Tag(name = "Movements", description = "Cadastro e consulta de movimentações")
public class MovementController {

    private final MovementService movementService;
    private final ClientRepository clientRepository;
    private final MovementRepository movementRepository;

    @Operation(summary = "Criar movimentação para um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimentação criada"),
            @ApiResponse(responseCode = "400", description = "Cliente/Conta não encontrada")
    })
    @PostMapping("/{clientId}")
    public ResponseEntity<MovementResponseDTO> create(@PathVariable Long clientId,
                                                      @RequestBody MovementCreateDTO dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Movement m = movementService.createForClient(client, dto);

        MovementResponseDTO resp = new MovementResponseDTO();
        resp.id = m.getId();
        resp.type = m.getType().name();
        resp.amount = m.getAmount();
        resp.description = m.getDescription();
        resp.date = m.getDate();

        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Listar movimentações do cliente com filtros opcionais (período e tipo)")
    @ApiResponse(responseCode = "200", description = "Lista de movimentações")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<MovementResponseDTO>> list(
            @PathVariable Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate end,
            @RequestParam(required = false) MovementType type) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        List<Movement> list;
        if (start != null && end != null && type != null) {
            list = movementRepository.findByClientAndTypeAndDateBetween(client, type, start, end);
        } else if (start != null && end != null) {
            list = movementRepository.findByClientAndDateBetween(client, start, end);
        } else if (type != null) {
            list = movementRepository.findByClientAndType(client, type);
        } else {
            list = movementRepository.findByClient(client);
        }

        List<MovementResponseDTO> dto = list.stream().map(m -> {
            MovementResponseDTO d = new MovementResponseDTO();
            d.id = m.getId();
            d.type = m.getType().name();
            d.amount = m.getAmount();
            d.description = m.getDescription();
            d.date = m.getDate();
            return d;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dto);
    }
}
