package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.dto.MovementCreateDTO;
import com.mv.financeiro_controladoria.application.dto.MovementResponseDTO;
import com.mv.financeiro_controladoria.application.service.MovementService;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.model.Movement;
import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movements")
public class MovementController {

    private final MovementService movementService;
    private final ClientRepository clientRepository;

    public MovementController(MovementService movementService, ClientRepository clientRepository) {
        this.movementService = movementService;
        this.clientRepository = clientRepository;
    }

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
}
