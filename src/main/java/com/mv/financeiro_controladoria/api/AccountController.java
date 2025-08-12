package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.dto.AccountDTO;
import com.mv.financeiro_controladoria.application.service.AccountService;
import com.mv.financeiro_controladoria.domain.model.Account;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.repository.AccountRepository;
import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "CRUD de contas bancárias de clientes")
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Operation(summary = "Criar conta para um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta criada"),
            @ApiResponse(responseCode = "400", description = "Cliente não encontrado")
    })
    @PostMapping("/clients/{clientId}/accounts")
    public ResponseEntity<Account> create(@PathVariable Long clientId, @RequestBody AccountDTO dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Account acc = new Account();
        acc.setClient(client);
        acc.setBank(dto.bank);
        acc.setNumber(dto.number);
        acc.setActive(true);
        Account saved = accountRepository.save(acc);
        return ResponseEntity.created(URI.create("/api/accounts/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Atualizar conta (bloqueado se já houver movimentações ou se inativa)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta atualizada"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada")
    })
    @PutMapping("/accounts/{id}")
    public ResponseEntity<Account> update(@PathVariable Long id, @RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.update(id, dto));
    }

    @Operation(summary = "Exclusão lógica da conta (marca como inativa)")
    @ApiResponse(responseCode = "204", description = "Conta inativada")
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteLogical(@PathVariable Long id) {
        accountService.deleteLogical(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar contas por cliente")
    @ApiResponse(responseCode = "200", description = "Lista de contas")
    @GetMapping("/clients/{clientId}/accounts")
    public ResponseEntity<List<Account>> listByClient(@PathVariable Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return ResponseEntity.ok(accountRepository.findByClient(client));
    }

    @Operation(summary = "Buscar conta por ID")
    @ApiResponse(responseCode = "200", description = "Conta encontrada")
    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> get(@PathVariable Long id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        return ResponseEntity.ok(acc);
    }
}
