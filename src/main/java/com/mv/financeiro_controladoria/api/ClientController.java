package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.dto.ClientCreateDTO;
import com.mv.financeiro_controladoria.application.dto.ClientResponseDTO;
import com.mv.financeiro_controladoria.application.service.ClientService;
import com.mv.financeiro_controladoria.domain.model.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@RequestBody ClientCreateDTO dto) {
        Client c = service.create(dto);
        ClientResponseDTO resp = new ClientResponseDTO();
        resp.id = c.getId();
        resp.name = c.getName();
        resp.phone = c.getPhone();
        resp.personType = c.getPersonType().name();
        return ResponseEntity.ok(resp);
    }
}
